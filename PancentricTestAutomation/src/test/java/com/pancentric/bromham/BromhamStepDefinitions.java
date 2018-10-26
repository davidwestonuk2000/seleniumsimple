package com.pancentric.bromham;

import com.pancentric.bromham.actions.JPBMActions;
import com.pancentric.bromham.actions.StaffPortalActions;
import com.pancentric.bromham.runners.BromhamFunctionalTestRunner;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import org.openqa.selenium.WebDriver;
import com.pancentric.bromham.actions.QuoteAndApplyActions;
import com.pancentric.utilities.SeleniumWebdriver;
import com.pancentric.utilities.TestResults;
import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.When;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BromhamStepDefinitions {
    private WebDriver driver; //this is our selenium webdriver controlling our browsers
    private SeleniumWebdriver selenium; //this is our selenium instance
    private QuoteAndApplyActions quoteAndApply; //our navigation class
    private StaffPortalActions staffPortal;
    private TestResults results;
    public static Scenario scenario;

    // get the environment to execute against
    public static String environment = System.getProperty("environment");

    // get the browser to use
    public String browser = System.getProperty("browser");

    // project variables
    String jira = "BF";
    public static String projectName = "bromham";

    // Shared variables
    public Map<String,String> memberDetails = new HashMap<>();
    String policyNumber = "";

    @Before
    public void setup(Scenario scenario) throws Exception {

        this.scenario = scenario;
        System.setProperty("project", projectName);

        // TODO what to do about this
        selenium = new SeleniumWebdriver(browser, "http://www.google.co.uk",scenario.getName());
        driver = selenium.getDriver(); //get which driver we are using
        quoteAndApply = new QuoteAndApplyActions(selenium);
        staffPortal = new StaffPortalActions(selenium);
        results = new TestResults(selenium);
    }

    @After
    public void cleanUp(Scenario scenario) throws Exception {

        try {
            JPBMActions jpbm = new JPBMActions();
            List<String> workflows = jpbm.getAllWorkflowsForPolicy(memberDetails.get("policyId"));
            String workflowList = "";
            for (int i = 0; i < workflows.size(); i++) {
                workflowList = workflowList + workflows.get(i) + "\n";
                System.out.println(workflows.get(i));
            }
            scenario.write(workflowList);
        }
        catch (Exception e) {
            System.out.println("IN EXCPTION");
        }
        // update global stats
        if (scenario.isFailed()) {
            BromhamFunctionalTestRunner.failedTests++;
        }
        else {
            BromhamFunctionalTestRunner.passedTests++;
        }

        driver = selenium.getDriver(); //get which driver we are using
        //scenario.embed(selenium.takeScreenshotFullScreenAsByte(),"img/png");
        //results.Update(scenario, jira, projectName, environment, selenium.stackTrace, selenium.errorMessage, selenium.screenshot, false);
    }

    //////////////////////////////////
    // Quote & Apply Definitions
    //////////////////////////////////

    // create a new policy application through the FA portal
    @Given("^A new FA portal application of type (.*)$")
    public void createANewApplicationThroughFAPortal(String policyScenario) throws Exception {
        quoteAndApply.completeQuote(policyScenario);
        memberDetails = quoteAndApply.completeApplication();
        //then you just access the reversedMap however you like...
        String memberInfo = "";
        for (Map.Entry entry : memberDetails.entrySet()) {
            memberInfo = memberInfo + (entry.getKey() + ", " + entry.getValue()) + "\n";
        }
        scenario.write(memberInfo);

        staffPortal.openStaffPortal();
        staffPortal.login("Normal");
    }

    // create a new policy through the REST API
    @Given("^An existing policy of type (.*)$")
    public void createANewPolicyThroughAPI(String scenarios) throws Exception {
        memberDetails = quoteAndApply.createPolicyRest(scenarios);
        String memberInfo = "";
        for (Map.Entry entry : memberDetails.entrySet()) {
            memberInfo = memberInfo + (entry.getKey() + ", " + entry.getValue()) + "\n";
        }
        scenario.write(memberInfo);
        policyNumber = memberDetails.get("policyNumber");
        System.out.println(policyNumber);
        System.out.println(memberDetails.get("policyId"));

        // check for the workflow in jpbm - pause for policy creation
        JPBMActions jpbm = new JPBMActions();
        String memberName = memberDetails.get("title") + ". " + memberDetails.get("name") + " " + memberDetails.get("surname");
        memberName = memberName.replace("Prof.","Prof");
        jpbm.verifyStartWorkflowForPolicy(memberDetails.get("policyId"), memberName,30,5);

        // open staff portal and login and search for the created policy
        staffPortal.openStaffPortal();
        staffPortal.login("Normal");
        staffPortal.searchByPolicyId(policyNumber);
    }

    @Given("^all policies for type (.*)$")
    public void createBatchPolicies(String scenario) throws Exception {
        quoteAndApply.createManyPolicies(scenario);
    }

    @Given("^the payment history is in place from (.*) to (.*)$")
    public void backdatePayments(String from, String to) throws Exception {
        quoteAndApply.setupPaymentHistory(memberDetails.get("policyId"),from,to);
    }

    @Given("^all premiums have been paid to date$")
    public void backdatePaymentsNoInput() throws Exception {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        quoteAndApply.setupPaymentHistory(memberDetails.get("policyId"),memberDetails.get("start_date"),dateFormat.format(date));
    }


    //////////////////////////////////
    // Staff Portal Actions
    //////////////////////////////////

    // search in staff portal by policy id
    @When("^I search by policy number$")
    public void searchByPolicyNumber() throws Exception {
        staffPortal.searchByPolicyId(policyNumber);
        scenario.embed(selenium.takeScreenshotFullScreenAsByte(),"img/png");
    }

    // search in staff portal by member name
    @When("^I search by member name$")
    public void searchByMemberName() throws Exception {
        staffPortal.searchByMemmberName(memberDetails.get("name") + " " + memberDetails.get("surname"), memberDetails.get("date_of_birth"));
    }

    // i add a note on a policy
    @When("^I add a note on the policy$")
    public void addNoteToPolicy() throws Exception {
        staffPortal.addNotePolicy();
    }

    // verify policy note
    @Then("^the note is visible on the policy$")
    public void verifyNoteAddedToPolicy() throws Exception {
        staffPortal.verifyPolicyNote();
    }

    // i upload a document to a policy
    @When("^I upload a document to the policy$")
    public void addDocumentToPolicy() throws Exception {
        staffPortal.uploadADocumentToPolicy();
    }

    // i upload a document to a policy
    @Then("^the document is visible on the policy$")
    public void verifyDocumentAddedToPolicy() throws Exception {
        staffPortal.verifyPolicyDocumentUpload();
    }

    // cancel existing policy
    @When("^the member cancels their policy for reason (.*)$")
    public void cancelPolicy(String cancelReason) throws Exception {
        staffPortal.cancelPolicy(cancelReason, false);
    }

    // verify policy canceled
    @Then("^the policy status is updated to (.*)$")
    public void verifyPolicyStatus(String status) throws Exception {
        staffPortal.verfiyPolicyStatus(status);
    }

    // cancel existing policy and issue refund
    @When("^the member cancels their policy and a refund is issued$")
    public void cancelAndRefundPolicy() throws Exception {
        staffPortal.cancelPolicy("Other",true);
    }

    // verify refund shows
    @When("^the refund shows against the policy$")
    public void verifyCancelationRefund() throws Exception {
        staffPortal.verifyCancellationRefund();
    }

    // reinstate a canceled policy
    @When("^the member reinstates the policy$")
    public void reinstatePolicy() throws Exception {
        staffPortal.reinstateAPolicy("");
    }

    // reinstate a canceled policy
    @When("^the member requests a premium holiday$")
    public void addPremiumHoliday() throws Exception {
        staffPortal.addPremiumHoliday();
    }

    // reinstate a canceled policy
    @Then("^the premium holiday shows on the policy$")
    public void verifyPremiumHoliday() throws Exception {
        staffPortal.verifyPremiumHoliday();
    }

    // change address
    @When("^a member request to change address is submitted$")
    public void changeAddressRequest() throws Exception {
        staffPortal.changeAddress();
    }

    // verify change address
    @Then("^the change of address request is submitted$")
    public void verrifyChangeAddressRequest() throws Exception {
        staffPortal.verifyChangeOfAddress();
    }

    // change occupation
    @When("^the member requests to change occupation$")
    public void changeOccupation() throws Exception {
        staffPortal.changeOccupation();
    }

    // verify change occupation
    @Then("^the change of occupation request is submitted$")
    public void verifyChangeOccupation() throws Exception {
        staffPortal.verifyChangeOfOccupation();
    }

    // add new bank details
    @Then("^the new bank details are submitted$")
    public void verifyAddBankDetails() throws Exception {
        staffPortal.verifyBankDetailsAdd();
    }

    // add new bank details
    @When("^the member adds new bank details to their policy$")
    public void addBankDetails() throws Exception {
        staffPortal.addNewBankDetails();
    }

    // a new claim is accepted
    @When("^a new claim is accepted$")
    public void acceptClaim() throws Exception {
        staffPortal.createANewClaim();
    }

    // the claim is accepted and in payment
    @Then("the claim is in Accepted and in payment$")
    public void verifyClaimSetup() throws Exception {
        staffPortal.verifyClaimPaymentsSetup();
    }

    // the claim is accepted and in payment
    @When("the member increases their premium cover$")
    public void increasePremiumCover() throws Exception {
        staffPortal.addAPremiumTranch();
    }


    //////////////////////////////////
    // JPBM Workflows
    //////////////////////////////////

    // check that a workflow has been created
    @Then("^a (.*) workflow is created$")
    public void checkWorkflowCreated(String workflowName) throws Exception {
        selenium.wait(5);
        JPBMActions jpbm = new JPBMActions();
        jpbm.verifyWorkflowForPolicyByType(memberDetails.get("policyId"),workflowName,60,5);
    }


    //////////////////////////////////
    // Utilities
    //////////////////////////////////

    // get the jira tag

    public String GetJiraRef() {
        // Check if the scenario is linked to a JIRA issue.
        // If it is, the first scenario tag is extracted as the JIRA reference

        scenario.getSourceTagNames();

        String[] myStringArray = scenario.getSourceTagNames().toArray(new String[0]);
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
}