package com.pancentric.bromham.runners;

import com.github.mkolisnyk.cucumber.reporting.CucumberDetailedResults;
import com.github.mkolisnyk.cucumber.runner.ExtendedCucumberOptions;
import com.google.common.collect.ImmutableSet;
import com.pancentric.jirahelper.ConfluenceHelper;
import com.pancentric.utilities.AfterSuite;
import com.pancentric.utilities.BeforeSuite;
import com.pancentric.bromham.BromhamStepDefinitions;
import com.pancentric.utilities.ExtendedRunner;
import com.pancentric.utilities.FileDownloader;
import com.pancentric.utilities.PropertyFetcher;
//import com.webcerebrium.slack.NotificationException;
//import com.webcerebrium.slack.SlackMessage;
//import com.webcerebrium.slack.SlackMessageAttachment;
import com.webcerebrium.slack.Notification;
import com.webcerebrium.slack.NotificationException;
import com.webcerebrium.slack.SlackMessage;
import com.webcerebrium.slack.SlackMessageAttachment;
import cucumber.api.CucumberOptions;
import net.masterthought.cucumber.Configuration;
import net.masterthought.cucumber.ReportBuilder;
import net.masterthought.cucumber.Reportable;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
//import com.webcerebrium.slack.Notification;


@RunWith(ExtendedRunner.class)
//@RunWith(ExtendedParallelCucumber.class)
@ExtendedCucumberOptions(jsonReport = "target/cucumber.json",
                         jsonUsageReport = "target/cucumber-usage.json",
                         outputFolder = "target/", detailedReport = true,
                         detailedAggregatedReport = true,
                         overviewReport = true, overviewChartsReport = true, usageReport = true,
                         coverageReport = true, retryCount = 2,
                         screenShotLocation = "target",
                         screenShotSize = "10px", toPDF = true,pdfPageSize = "A4 Landscape")
@CucumberOptions(plugin =
                 { "html:target/cucumber-html-report", "junit:target/cucumber-junit.xml",
                   "json:target/cucumber.json",
                   "pretty:target/cucumber-pretty.txt",
                   "usage:target/cucumber-usage.json" },
                 features = { "target/generated-data/features/BF" },
                 monochrome = true, strict = true,
                 glue = { "com.pancentric.bromham" }, tags = { "@BF-3030" })

    public class BromhamFunctionalTestRunner {

        public static int failedTests = 0;
        public static int passedTests = 0;

        @BeforeSuite
        public static void setup() throws Exception, NotificationException {
            //NotificationException
            System.out.println("BEFORE!");
            SlackMessage message = new SlackMessage();
            SlackMessageAttachment attach = new SlackMessageAttachment("Test execution starting", "Automated Tests commencing in " + System.getProperty("environment")+" using " + System.getProperty("browser"), "#c0FFF0");
            attach.addMarkdown(ImmutableSet.of("title", "text"));
            message.getAttachments().add(attach);
            //(new Notification()).send(message);

            FileDownloader download = new FileDownloader();
            PropertyFetcher propertyfile = new PropertyFetcher();
            // locate the appropriate prop file
            String propfile = BromhamStepDefinitions.projectName + "/properties/"+ BromhamStepDefinitions.environment;
            String urlHREF = propertyfile.getPropValues(propfile,"dataFileLocation");
            download.fileDownloaderNonBrowser(urlHREF, "target/", "data.xlsx", 10);
        }

        @AfterSuite
        public static void tearDown() throws Exception, NotificationException {

            CucumberDetailedResults results = new CucumberDetailedResults();
            results.setOutputDirectory("target/");
            results.setOutputName("cucumber-results");
            results.setSourceFile("target/cucumber.json");
            results.setPdfPageSize("A3 Landscape");
            System.out.println(results.getPdfPageSize());
            System.out.println(results.getReportType());
            results.setScreenShotWidth("800px");
            String[] formats = new String[1];
            formats[0] = "pdf";
            results.execute(true, formats);

            String colour = "";
            if(passedTests==passedTests+failedTests) {
                colour = "#58D68D";
            }
            else if (failedTests==passedTests+failedTests) {
                colour = "#E74C3C";
            }
            else {
                colour = "#F4D03F";
            }

            System.out.println("PASSED TESTS: " + passedTests);
            System.out.println("FAILED TESTS: " + failedTests);

            ConfluenceHelper confluence = new ConfluenceHelper();
            String currentDir = System.getProperty("user.dir");
            String downloadUrl = "https://confluence.pancentric.com"+confluence.reUploadNewFile(currentDir + "/target/","cucumber-results-test-results.pdf","15007801","15007844");

            SlackMessage message = new SlackMessage();
            SlackMessageAttachment attach = new SlackMessageAttachment("Automated Tests completed in " + System.getProperty("environment"), "Tests Passed: " + passedTests + "\n" +
                    "Tests Failed: " + failedTests + "\n" +
                    downloadUrl, colour);
            attach.addMarkdown(ImmutableSet.of("title", "text"));
            message.getAttachments().add(attach);
           //(new Notification()).send(message);
        }
    }