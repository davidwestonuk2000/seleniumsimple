package com.pancentric.utilities;

//import com.atlassian.jira.rest.client.RestClientException;
//import com.atlassian.jira.rest.client.domain.Issue;

import com.atlassian.jira.rest.client.api.RestClientException;

import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.Attachment;

import com.pancentric.jirahelper.JiraHelper;

//import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static org.junit.Assert.*;

import com.pancentric.utilities.SeleniumWebdriver.Locators;

import com.pancentric.utilities.SeleniumWebdriver;

import cucumber.api.Scenario;

import java.net.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import com.pancentric.utilities.SeleniumWebdriver.trackType;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;



import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.openqa.selenium.WebDriver;

public class TestResults {

    private SeleniumWebdriver selenium;
    //this is our selenium webdriver controlling our browsers
    private WebDriver driver;

    //our constructor getting our selenium instance, and the driver to be used

    public TestResults(SeleniumWebdriver seleniumx) {
        this.selenium = seleniumx;
        this.driver = selenium.getDriver();
    }

    public final byte[] GetScreenshot() {
        final byte[] screenshot =
            ((TakesScreenshot)driver).getScreenshotAs(OutputType.BYTES);
        return screenshot;
    }

    public void Update(Scenario scenario, String jira, String projectName, String environment, String stackTrace, String errorMessage, byte[] screenshot, boolean raiseBug) throws Exception {
        // Executes after each scenario is run (passed or failed)
        Collection<String> tags = scenario.getSourceTagNames();
        String jiraRef = GetJiraRef(tags, jira);
        String transition = "";
        
        scenario.write(this.selenium.analyzeLog("SEVERE","").toString());

        byte[] screenshotNew = selenium.closeTheBrowser(scenario.isFailed());

        if (scenario.isFailed()) {
            scenario.embed(screenshotNew, "image/png");
            transition = "Failed";
        }

        else {          
          transition = "Passed";
          raiseBug = false;
      }
        
      if (jiraRef != "") {
         UpdateJira(transition, scenario, jiraRef, stackTrace, errorMessage, screenshotNew, raiseBug, jira, environment);
      }
    }

    private static String GetJiraRef(Collection<String> tags, String jira) {
        // Check if the scenario is linked to a JIRA issue.
        // If it is, the first scenario tag is extracted as the JIRA reference
        String[] myStringArray = tags.toArray(new String[0]);
        String jiraRef = "";
        int i = 0;
        String allTags = "";
        if (myStringArray.length == 0) {
            allTags = "Unknown";
        } else {
            while (i < myStringArray.length) {
                if (myStringArray[i].length()>jira.length()) {
                    if ((myStringArray[i].substring(1,jira.length() + 1).equals(jira))) {
                        jiraRef = myStringArray[i].substring(1);
                    }
                }
                allTags = allTags + " " + myStringArray[i];
                i++;
            }
        }
        return jiraRef;
    }

    private void UpdateJira(String transition, Scenario scenario, String jiraRef, String stackTrace, String errorMessage, byte[] screenshot, boolean raiseBug, String projectKey, String environment) throws Exception {
        // If the scenario is linked to a JIRA issue, initialise the JIRA helper
        JiraHelper iMJiraHelper = new JiraHelper("Basic");
        String comment = "";
        assertTrue("IMJiraHelper not initialized correclty!!!", iMJiraHelper != null);
        // Get the associated JIRA Issue

        try {
            Issue issue = iMJiraHelper.getIssue(jiraRef);
            if (issue != null) {

                // If the scenario is failed, update the JIRA issue status to failed and attach a screenshot
                if (transition.equals("Failed")) {
                    if (screenshot!=null) {                      
                       URI attachUri = issue.getAttachmentsUri();
                       
                        try {                            
                          iMJiraHelper.attachScreenshot(attachUri, screenshot);
                        }
                        catch (URISyntaxException e) {
                          System.out.println(e.getMessage());
                        }
                    }
                    comment = "Test (" + scenario.getName() + ") Failed in " + environment + "\n" + stackTrace + errorMessage;
                    if (stackTrace!=null) {
                        comment = comment + stackTrace;
                    }
                    
                  if (raiseBug == true) {
                    String newIssue =iMJiraHelper.createIssue(projectKey, scenario.getName(), errorMessage, stackTrace, screenshot, environment);
                    System.out.println("New defect raised : " + newIssue);
                    iMJiraHelper.linkIssues(newIssue,jiraRef,"Blocker");
                    //linkIssues(newIssue, jiraRef, );
                  }
                }

                if (transition.equals("Passed")) {
                    comment = "Test (" + scenario.getName() + ") Passed in " + environment;
                }
                
              // Transition the issue
              iMJiraHelper.transitionIssue(issue, comment, transition);
            }
        }

        catch (RestClientException r) {
            System.out.println(r.getMessage());
            System.out.println("Issue not found");
        }
    }
    
    public static void buildFeatureFiles(String projectKey, String issueType) throws Exception {

        String startPath = "target//generated-data//features//" + projectKey + "//";

        // new instance of the jira helper
        JiraHelper jira = new JiraHelper("Basic");

        //  retreive all features from jira
        Map<Map<String, String>, List<Map<String, String>>> allIssuesAndSubTasks = jira.getAllIssuesAndSubTasks(projectKey, issueType);
        Set<Map<String, String>> features = allIssuesAndSubTasks.keySet();

        ExecutorService executor = Executors.newFixedThreadPool(30);

        // a counter for the features
        //int featureCounter = 1;

        // loop through all of the found features
        for (Iterator<Map<String, String>> featureIt = features.iterator(); featureIt.hasNext(); ) {
            Map<String, String> feature = featureIt.next();
            Runnable worker = new MyRunnable(feature, startPath, allIssuesAndSubTasks);
            executor.execute(worker);
            //new Thread(worker).start();
        }
        executor.shutdown();
        // Wait until all threads are finish
        while (!executor.isTerminated()) {

        }
        System.out.println("\nFinished all threads");
    }

    public static class MyRunnable implements Runnable {
            private final Map<String, String> feature;
            private final String startPath;
            private final  Map<Map<String, String>, List<Map<String, String>>> allIssuesAndSubTasks;
            private int featureCounter;

            MyRunnable(Map<String, String> feature, String startPath,  Map<Map<String, String>, List<Map<String, String>>> allIssuesAndSubTasks) {
                this.feature = feature;
                this.startPath = startPath;
                this.allIssuesAndSubTasks = allIssuesAndSubTasks;
                //this.featureCounter = featureCounter;
            }

            @Override
            public void run() {
                // if the feature is automated
                if (feature.get("Automated").equals("Yes")) {

                    // feature name
                    String featureName = feature.get("Summary").toLowerCase();
                    featureName = featureName.replace(" ", "-");
                    // tags
                    String[] tags = feature.get("Labels").split(",");

                    // create the feature file
                    String outfile = feature.get("Counter")+"-"+featureName+".feature";
                    // create the new file
                    File file = new File(startPath + outfile);
                    file.getParentFile().mkdirs();
                    FileWriter fw = null;
                    try {
                        fw = new FileWriter(file, false);
                    }
                    catch (Exception e) {
                    }
                    // creater the print writer
                    PrintWriter pw = new PrintWriter(fw, true);

                    int tagsCounter=0;

                    //pw.write("@"+feature.get("IssueKey"));
                    tags = feature.get("Labels").split(",");
                    // loop through each label
                    while (tagsCounter<tags.length) {
                        // remove unwanted characters from the jira labels
                        // for use in @tags
                        tags[tagsCounter]=tags[tagsCounter].replace("[", "");
                        tags[tagsCounter]=tags[tagsCounter].replace("]", "");
                        tags[tagsCounter]=tags[tagsCounter].replace(" ", "");
                        if(tags[tagsCounter].length()>2) {
                            pw.write(" @"+tags[tagsCounter]);
                        }
                        tagsCounter++;
                    }

                    pw.println();
                    // write the feature name
                    pw.write("Feature: " + feature.get("Summary"));
                    pw.println();
                    // wrtie the featue description
                    if (feature.get("Description").equals("")) {
                        pw.println("No feature description");
                    }
                    else {
                        pw.write(feature.get("Summary"));
                    }

                    pw.println();

                    // get all the scenarios for the feature
                    List<Map<String,String>> featureScenarios = allIssuesAndSubTasks.get(feature);

                    // loop through the found scenarios for each found feature
                    for (Iterator<Map<String,String>> scenarioIt = featureScenarios.iterator(); scenarioIt.hasNext(); ) {
                        pw.println();
                        // get the next scenario
                        Map<String,String> scenario = scenarioIt.next();
                        // only add the automated scenarios
                        if(scenario.get("Automated").equals("Yes")) {
                            // tags
                            pw.write("@"+scenario.get("IssueKey"));
                            tags = scenario.get("Labels").split(",");
                            tagsCounter=0;
                            while (tagsCounter<tags.length) {
                                tags[tagsCounter]=tags[tagsCounter].replace("[", "");
                                tags[tagsCounter]=tags[tagsCounter].replace("]", "");
                                tags[tagsCounter]=tags[tagsCounter].replace(" ", "");
                                if(tags[tagsCounter].length()>2) {
                                    pw.write(" @"+tags[tagsCounter]);
                                }
                                tagsCounter++;
                            }

                            pw.println();
                            if(scenario.get("Description").contains("Examples")) {
                                pw.print("Scenario Outline: " + scenario.get("Summary"));
                            }
                            else {
                                pw.print("Scenario: " + scenario.get("Summary"));
                            }
                            pw.println();
                            pw.print(scenario.get("Description"));
                            pw.println();
                        }
                    }

                    //Flush the output to the file
                    pw.flush();
                    //Close the Print Writer
                    pw.close();
                    //Close the File Writer
                    try {
                        fw.close();
                    }
                    catch(Exception e) {

                    }
                }
                // go to the next feature
                //featureCounter++;

            }
    }
}


