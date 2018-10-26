package com.pancentric.jirahelper;

import com.atlassian.jira.rest.client.api.domain.input.TransitionInput;
import com.atlassian.jira.rest.client.api.domain.input.FieldInput;
import com.atlassian.jira.rest.client.api.GetCreateIssueMetadataOptionsBuilder;
import com.atlassian.jira.rest.client.api.IssueRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClientFactory;
import com.atlassian.jira.rest.client.api.VersionRestClient;
import com.atlassian.jira.rest.client.api.domain.Attachment;
import com.atlassian.jira.rest.client.api.domain.Worklog;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import com.atlassian.jira.rest.client.api.AuthenticationHandler;
import com.atlassian.httpclient.api.Request;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import com.atlassian.jira.rest.client.api.domain.BasicComponent;
import com.atlassian.jira.rest.client.api.domain.BasicIssue;
import com.atlassian.jira.rest.client.api.domain.BasicPriority;
import com.atlassian.jira.rest.client.api.domain.BasicProject;
import com.atlassian.jira.rest.client.api.domain.BasicUser;
import com.atlassian.jira.rest.client.api.domain.CimIssueType;
import com.atlassian.jira.rest.client.api.domain.CimProject;
import com.atlassian.jira.rest.client.api.domain.Comment;
import com.atlassian.jira.rest.client.api.domain.Field;
import com.atlassian.jira.rest.client.api.domain.EntityHelper;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.input.LinkIssuesInput;
import com.atlassian.jira.rest.client.api.domain.IssueFieldId;
import com.atlassian.jira.rest.client.api.domain.IssueField;
import com.atlassian.jira.rest.client.api.domain.IssueType;
import com.atlassian.jira.rest.client.api.domain.SearchResult;
import com.atlassian.jira.rest.client.api.domain.Subtask;
import com.atlassian.jira.rest.client.api.domain.Transition;
import com.atlassian.jira.rest.client.api.domain.IssueLink;

import com.atlassian.jira.rest.client.api.domain.Version;

import com.atlassian.jira.rest.client.api.domain.input.ComplexIssueInputFieldValue;
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder;
import com.atlassian.jira.rest.client.api.domain.input.TransitionInput;
import com.atlassian.jira.rest.client.internal.ServerVersionConstants;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;

import com.atlassian.util.concurrent.Promise;

import com.google.common.collect.Iterables;

import static org.junit.Assert.*;

import java.io.*;

import java.net.InetAddress;
import java.net.URI;

import java.net.URISyntaxException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.oauth.OAuth;
import net.oauth.OAuth.Parameter;
import net.oauth.OAuthAccessor;
import net.oauth.OAuthConsumer;
import net.oauth.OAuthMessage;
import net.oauth.client.OAuthClient;
import net.oauth.OAuthServiceProvider;
import net.oauth.http.HttpMessage;
import net.oauth.signature.RSA_SHA1;
import net.oauth.ParameterStyle;

public class JiraHelper {

    private JiraHelper instance;
    public JiraRestClient restClient;
      private final String consumerKey;
      private final String privateKey;
      private final String baseUrl;
      private final String password;
      private final String username;
      private final JiraRestClientFactory factory;
      private final String SERVLET_BASE_URL = "/plugins/servlet/";
      private String callback = "";
      private OAuthAccessor accessor;
      public Map<Map<String,String>,List<Map<String,String>>> jiraItems = new HashMap<Map<String,String>,List<Map<String,String>>>();

    public JiraHelper(String authType) throws URISyntaxException {
        super();
        baseUrl = "https://jira.XXXX.com";
        privateKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAJ9PSvuHVLwa2MMC\n" +
                "38fkx+vN4yWhq3KY1CwWNSQP46VCMPdT3eXce84D4wPIzkhXR3jzDDnYZnPUMCrj\n" +
                "+96P/KswwqFCv8WdYOkKOLhH3jtAsv4LxEYmZ+g4GXGihbZci5Dq5PP85OQkEKEQ\n" +
                "VW19W42KjWdSK9nJHKJthiuHAcojAgMBAAECgYAu85yiwwDbK4Jk/wzbCfdN7PEc\n" +
                "Hyi7boVhtO3WggRSqU5rJkGIxFGMpyKZ8+2v7mE3KVKaHcgcLRZBHc4WixMpHdNn\n" +
                "VUzo2nHUmXYgHquGZaRxvr3uTxn8kzp+NXEiGB1wpjrwppOGb7/e42K6//TnxPIF\n" +
                "iKYfUCq2ANjLkAJrsQJBANTJm4IuZAnvywM8O5Qs4lW02TJLCpHdpRPw4ToKLBm1\n" +
                "lYILyRA0x83o3BSaCLhVpT7Zh1E8G1fpmoeYoNOXe+sCQQC/qXvBi6PheNgAuIbD\n" +
                "roX6qTiEEcWREEpcno7l5nB3MfRPUDq+MNU2VBvVQscQpr9oxXTnIrgW91Uzd1Zx\n" +
                "b/SpAkA8qsJQimNxRdHOuVSPOYDKSMaIBUcdMWFIXywHvTC3n8LhkfSgTzwMI/Dj\n" +
                "WstTqu9zDNf8vDNbjDnHoSdxTEZDAkAMaJmljzfd+ifp7Ah1lNAByYDqNAhZeveF\n" +
                "hZJ02fWAEhDiLayP8bNsIAfpR7iBoHoV+2q1KC9I9Vxjx58mGvx5AkEAhrbereuM\n" +
                "weYV+iieoV2Kb0iDFvFmZx+4x4HfaFsUhqt5GaaGOHS2w7DAr6uaYqvjBHZIa6eB\n" +
                "FiBGm0VKAWreBQ==";;
        consumerKey = "OauthKey";
        
        factory = new AsynchronousJiraRestClientFactory();
        final URI jiraServerUri = new URI(baseUrl);
    
        password = "XXXX";
        username = "XXXX";
        
        if (authType == "Oauth") {              
          restClient = factory.create(jiraServerUri, new AuthenticationHandler() {
                      
                      @Override
                      public void configure(Request request) {
                          try {
                              OAuthAccessor accessor = getAccessor();
                              accessor.accessToken = "AVniyXsnzeEcJC4575uFlQkXxalvoyc0";
                              OAuthMessage request2 = accessor.newRequestMessage(null, request.getUri().toString(), Collections.<Map.Entry<?, ?>>emptySet(), request.getEntityStream());
                              Object accepted = accessor.consumer.getProperty(OAuthConsumer.ACCEPT_ENCODING);
                              if (accepted != null) {
                                  request2.getHeaders().add(new OAuth.Parameter(HttpMessage.ACCEPT_ENCODING, accepted.toString()));
                              }
                              Object ps = accessor.consumer.getProperty(OAuthClient.PARAMETER_STYLE);
                              ParameterStyle style =
                      (ParameterStyle)((ps == null) ? ParameterStyle.BODY
                                      : Enum.valueOf(ParameterStyle.class, ps.toString()));
                              HttpMessage httpRequest = HttpMessage.newRequest(request2, style);
                              for ( Entry<String, String> ap : httpRequest.headers)
                                  request.setHeader(ap.getKey(), ap.getValue());
                              request.setUri( httpRequest.url.toURI() );
                          } catch (Exception e) {
                              e.printStackTrace();
                          }
                      }
                  });
        }
        
        else {
          
          try {
            restClient = factory.createWithBasicHttpAuthentication(jiraServerUri, username, password);
          }
          catch (NullPointerException e) {
              System.out.println("DAVID");
            e.getMessage();
          }
        }
    }
    
  private final OAuthAccessor getAccessor()
      {
          if (accessor == null)
          {
              OAuthServiceProvider serviceProvider = new OAuthServiceProvider(getRequestTokenUrl(), getAuthorizeUrl(), getAccessTokenUrl());
              OAuthConsumer consumer = new OAuthConsumer(callback, consumerKey, null, serviceProvider);
              consumer.setProperty(RSA_SHA1.PRIVATE_KEY, privateKey);
              consumer.setProperty("user_id", "david.weston");
              consumer.setProperty("xoauth_requestor_id", "david.weston");
              consumer.setProperty(OAuth.OAUTH_SIGNATURE_METHOD, OAuth.RSA_SHA1);
              accessor = new OAuthAccessor(consumer);
          }
          return accessor;
      }
      private String getAccessTokenUrl()
      {
          return baseUrl + SERVLET_BASE_URL + "/oauth/access-token";
      }
      private String getRequestTokenUrl()
      {
          return  baseUrl + SERVLET_BASE_URL + "/oauth/request-token";
      }
      public String getAuthorizeUrlForToken(String token)
      {
          return getAuthorizeUrl() + "?oauth_token=" + token;
      }
      private String getAuthorizeUrl() {return baseUrl + SERVLET_BASE_URL + "/oauth/authorize";}
      

    // create a new issue in jira for a failed test

    public String createIssue(String projectKey, String scenario, String message, String stackTrace, byte[] attachment, String environment) throws Exception {
        
        final IssueRestClient issueClient = restClient.getIssueClient();
        final Iterable<CimProject> metadataProjects =
            issueClient.getCreateIssueMetadata(new GetCreateIssueMetadataOptionsBuilder().withProjectKeys(projectKey).withExpandedIssueTypesFields().build()).claim();

        // select project and issue
        assertEquals(1, Iterables.size(metadataProjects));
        final CimProject project = metadataProjects.iterator().next();     
        final CimIssueType issueType = EntityHelper.findEntityByName(project.getIssueTypes(), "Bug");
        String description = "";    
        
        if (stackTrace != null) {
            if (stackTrace.length() > 200) {
                description = "Failed cucumber scenario: " + scenario + " : " + message + stackTrace.subSequence(0, 200);
            }
            
            else {
                description = "Failed cucumber scenario: " + scenario + " : " + message + stackTrace;
            }
        }
        else {
            description = "Failed cucumber scenario: " + scenario;
        }
                
        final String summary = "Failed cucumber scenario: " + scenario;
        // check if the isuse has already been raised
                
        Promise<SearchResult> searchJqlPromise = restClient.getSearchClient().searchJql("project = " + projectKey + " AND status != Done AND issuetype = " + issueType.getName() + " AND summary ~ \"" + summary + "\" ORDER BY Rank ASC");
        System.out.println("project = " + projectKey + " AND status != Done AND issuetype = " + issueType.getName() + " AND summary ~ \"" + summary + "\" ORDER BY created DESC");
        boolean found = false;
        String existingIssueKey = "";
        // gets each issue in turn
                
        if (searchJqlPromise.claim().getTotal() > 0) {
            existing:
            for (BasicIssue issue : searchJqlPromise.claim().getIssues()) {
            Issue fullIssue = restClient.getIssueClient().getIssue(issue.getKey()).claim();
                // get the issue description
                //if (fullIssue.getDescription().equals(description)) {
                    found = true;
                    existingIssueKey = fullIssue.getKey();
                    break existing;
                //}            
            }
        }
               
        if (found == false) {
            System.out.println("No existing issue");
            
            // grab the first priority
            final Iterable<Object> allowedValuesForPriority = issueType.getField(IssueFieldId.PRIORITY_FIELD).getAllowedValues();
            System.out.println("No existing issued");
            assertTrue(allowedValuesForPriority.iterator().hasNext());
            System.out.println("No existing issuec");
            BasicPriority priority = (BasicPriority)allowedValuesForPriority.iterator().next();
            System.out.println("No existing issueb");
            // get allowed value for tempo account id
            //Iterable<Object> allowedValues = issueType.getFields().get("customfield_11610").getAllowedValues();
            //String accountId = (allowedValues.iterator().next().toString());
            //accountId = accountId.substring(accountId.indexOf(":")+1, accountId.indexOf(","));

            // build issue input
            // prepare IssueInput
            final IssueInputBuilder issueInputBuilder = new IssueInputBuilder(project, issueType,summary);
                //.setFieldValue("customfield_11610", ComplexIssueInputFieldValue.with("id","125")) 
                //.setFieldValue("customfield_11610", "125")
               // .setDescription(description)
                //.setComponents(component)
                //.setPriority(priority)
                //.setFieldValue("labels",Arrays.asList("AutomatedTest", "Cucumber", environment));
            System.out.println("No existing issuea");
            // create
            final BasicIssue basicCreatedIssue = issueClient.createIssue(issueInputBuilder.build()).claim();
            System.out.println("No existing issues");
            assertNotNull(basicCreatedIssue.getKey());
            System.out.println("No existing issued");
            // get issue and check if everything was set as we expected
            final Issue createdIssue = issueClient.getIssue(basicCreatedIssue.getKey()).claim();
            assertNotNull(createdIssue);
            URI attachmentsUri = createdIssue.getAttachmentsUri();
            attachScreenshot(attachmentsUri, attachment);
            return createdIssue.getKey();
        }
    
        else {
            return existingIssueKey;
        }
    
    }

    public Issue getIssue(String issueNames) {
        Issue issue = restClient.getIssueClient().getIssue(issueNames).claim();
        return issue;
    }

    // attach a screenshot to jira issue
    
    public void attachScreenshot(URI uri,
                                 byte[] attachment) throws Exception {
        
        String attachmentText = new String(attachment, "UTF-8");
        if (attachmentText.equals("Screenshot not available")==false) {
            restClient.getIssueClient().addAttachment(uri,
                                                      new ByteArrayInputStream(attachment),
                                                      "screenshot.png").claim();
        }
  }
    
  public void transitionIssue(Issue issue, String comment, String transitionName) throws Exception {
      // now let's start progress on this issue
        System.out.println(comment);
      Comment.valueOf(comment);

      final Iterable<Transition> transitions =
          restClient.getIssueClient().getTransitions(issue.getTransitionsUri()).claim();
      final Transition resolveIssueTransition =
          getTransitionByName(transitions, transitionName);
      if (resolveIssueTransition != null) {
          restClient.getIssueClient().transition(issue.getTransitionsUri(),
                                                 new TransitionInput(resolveIssueTransition.getId(),
                                                                     Comment.valueOf(comment)));

      } else {
          System.out.println("Issue transition not found");
      }
    }

    private static Transition getTransitionByName(Iterable<Transition> transitions,
                                                  String transitionName) {
        for (Transition transition : transitions) {
            if (transition.getName().equals(transitionName)) {
              System.out.println(transition.getName());
                return transition;
            }
        }
        return null;
    }
    
    // get all issues and subtasks from a project of a certain type
    
    public Map<Map<String,String>,List<Map<String,String>>> getAllIssuesAndSubTasks(String projectKey, String issueType) throws Exception {
        // holds the jira items mapping features to their scenarios
        System.out.println(projectKey);
        // execute jql query in jira
        Promise<SearchResult> searchJqlPromise = restClient.getSearchClient().searchJql("project = " + projectKey + " AND issue in issuesParents(\"issuetype =AutomatedTest\") ORDER BY Rank ASC",2000,0,null);
        // counts the features
        int featureCounter = 1;
        System.out.println("HERE");
        // gets each issue in turn
        searchJqlPromise.claim();
        System.out.println("HERE");

        ExecutorService executor = Executors.newFixedThreadPool(10);
        System.out.println("HERE");

        // loop through all of the found features
        for (BasicIssue issue : searchJqlPromise.claim().getIssues()) {
            System.out.println("HERE");
            //Map<String, String> feature = featureIt.next();
            Thread.sleep(500);
            Runnable worker = new MyRunnable(issue, featureCounter);
            executor.execute(worker);
            featureCounter++;
            //new Thread(worker).start();
        }
        executor.shutdown();
        // Wait until all threads are finish
        while (!executor.isTerminated()) {

        }
        System.out.println("\nFinished all threads");

        return jiraItems;
    }

    public class MyRunnable implements Runnable {
        private  BasicIssue issue;
        private int featureCounter;

        MyRunnable(BasicIssue issue, int featureCounter) {
            this.issue = issue;
            this.featureCounter = featureCounter;
            //this.featureCounter = featureCounter;
        }

        @Override
        public void run() {
            Map<String,String> feature = new HashMap<String,String>();
            // get the issue unique key for the feature
            feature.put("IssueKey", issue.getKey());
            System.out.println("Getting parent issue: "+issue.getKey());
            // get the full jira issue for the feature
            Issue fullIssue = restClient.getIssueClient().getIssue(issue.getKey()).claim();
            // get the issue summary
            System.out.println(fullIssue.getSummary());
            feature.put("Summary", fullIssue.getSummary().replace(":","").replace("(","").replace(")",""));
            // get the issue description
            feature.put("Description", fullIssue.getDescription());
            // get the issue labels
            feature.put("Labels", fullIssue.getLabels().toString());
            // add a feature counter to determine the order of execution
            feature.put("Counter", Integer.toString(featureCounter));
            feature.put("Automated", "Yes");
            // get all the issue subtasks
            Iterable<Subtask> subtasks = fullIssue.getSubtasks();

            int j = 0;
            Iterable<IssueLink> IssueLinks = fullIssue.getIssueLinks();
            Iterator linksIterator = IssueLinks.iterator();

            int i=0;
            // iterate over subtasks
            Iterator subtasksIterator = subtasks.iterator();
            List<Map<String,String>> scenarios = new ArrayList<Map<String,String>>();

            while (subtasksIterator.hasNext()) {
                Map<String,String> scenario = new HashMap<String,String>();
                Subtask subtask = Iterables.get(subtasks, i);
                // get the scenario summary
                if (subtask.getIssueType().getName().equals("AutomatedTest")) {
                    scenario.put("Summary", subtask.getSummary().replace("(","").replace(")",""));
                    // get the feature issue key
                    scenario.put("IssueKey", subtask.getIssueKey());
                    // get the full issue for the scenario
                    System.out.println("Getting sub issue: "+ subtask.getIssueKey());
                    Issue fullSubtask = restClient.getIssueClient().getIssue(subtask.getIssueKey()).claim();
                    // add subtask description
                    scenario.put("Description", fullSubtask.getDescription());
                    scenario.put("Automated", "Yes");
                    // try to get the issue priority
                    String priority = "";
                    try {
                        priority = fullSubtask.getPriority().getName();
                    }
                    catch (Exception e) {
                        priority = "Blocker";
                    }
                    scenario.put("Priority", priority);
                    // add the task labels
                    scenario.put("Labels", fullSubtask.getLabels().toString());
                    // add the status
                    String status = fullSubtask.getStatus().getName().replace(" ", "").replace(".", "");
                    scenario.put("Status", status);
                    // add the fix version
                    String fixVersion = "";
                    try {

                        Iterator<Version> versionIterator = fullSubtask.getFixVersions().iterator();
                        while ((versionIterator.hasNext()) ) {
                            Version version = versionIterator.next();
                            fixVersion = fixVersion + version.getName()+",";
                        }
                        fixVersion = fixVersion.substring(0,fixVersion.length()-1);
                    }
                    catch (Exception e) {
                        fixVersion = "";
                    }
                    scenario.put("FixVersion", fixVersion);
                    // add priority to labels if present
                    if (priority!="") {
                        scenario.put("Labels", scenario.get("Labels") + "," + priority);
                    }
                    // add fix version to labels if present
                    if (fixVersion!="") {
                        scenario.put("Labels", scenario.get("Labels") + "," + fixVersion);
                    }
                    // add status to labels if present
                    if (status!="") {
                        scenario.put("Labels", scenario.get("Labels") + "," + status);
                    }

                    scenarios.add(scenario);
                }

                subtasksIterator.next();
                i++;
            }

            jiraItems.put(feature,scenarios);
        }
    }
    
    // links two jira issues and any story linked to the original issue
    
    public void linkIssues(String issueOne, String issueTwo, String linkName) throws Exception {
        
        final IssueRestClient issueClient = restClient.getIssueClient();
        
        issueClient.linkIssue(new LinkIssuesInput(issueOne, issueTwo, linkName, null)).claim();
        
        final Issue originalIssue = issueClient.getIssue(issueTwo).claim();
        Iterator<IssueLink> links = (originalIssue.getIssueLinks().iterator());
        while ((links.hasNext()) ) {
            IssueLink link = links.next();
            if (link.getIssueLinkType().getName().equals("Tests")) {
                issueClient.linkIssue(new LinkIssuesInput(issueOne, link.getTargetIssueKey(), linkName, null)).claim();
            }
        }
    }
    
    // get an attachment for an issue
    
    public URI getIssueAttachment(String issueKey, String attachmentName) {
        
        URI uri = null;
        
        final IssueRestClient issueClient = restClient.getIssueClient();
        final Issue issue = issueClient.getIssue(issueKey).claim();
        Iterable iterable = issue.getAttachments();
        Iterator iterator = iterable.iterator();
        while (iterator.hasNext()) {
            Attachment attachment = (Attachment)iterator.next();
            System.out.println(attachment.getFilename());
            if (attachment.getFilename().equals(attachmentName)) {
                System.out.println("FOUND BASELINE");
                uri = attachment.getContentUri();
                break;
            }
        }

        return uri;
    }
    
    // attach the baseline image

    public void attachBaseline(String issueKey, byte[] attachment, String testName) throws Exception {
        
        final IssueRestClient issueClient = restClient.getIssueClient();
        final Issue issue = issueClient.getIssue(issueKey).claim();
        URI uri = issue.getAttachmentsUri();
        
        if(attachment != null) {
            String attachmentText = new String(attachment, "UTF-8");
            if (attachmentText.equals("Screenshot not available")==false) {
                restClient.getIssueClient().addAttachment(uri,new ByteArrayInputStream(attachment),testName+"-Baseline.png").claim();
            }   
        }
    }

}
