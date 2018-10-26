package com.pancentric.bromham.actions;

import com.pancentric.bromham.BromhamStepDefinitions;
import com.pancentric.utilities.ExcelFetcher;
import com.pancentric.utilities.PropertyFetcher;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import com.pancentric.utilities.SeleniumWebdriver.Locators;
import com.pancentric.utilities.SeleniumWebdriver;
import org.openqa.selenium.WebElement;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class StaffPortalActions {
    //this is our selenium instance
    private SeleniumWebdriver selenium;
    //this is our selenium webdriver controlling our browsers
    private WebDriver driver;
    private PropertyFetcher propertyfile = new PropertyFetcher();
    private String propfile;
    String staffPortalUrl;
    private int selectedRow = 0;
    private String weeklyBenefit = "";

    //our constructor getting our selenium instance, and the driver to be used

    public StaffPortalActions(SeleniumWebdriver selenium) throws Exception {
        this.selenium = selenium;
        //this.driver = selenium.getDriver();
        // locate the appropriate prop file
        propfile = System.getProperty("project") + "/properties/"+ System.getProperty("environment");
        staffPortalUrl = propertyfile.getPropValues(propfile, "staffPortalUrl");
    }

    public void openStaffPortal() throws Exception {
        selenium.loadPageSameWindow(staffPortalUrl);
    }

    // login to the staff portal
    public boolean login(String loginType) throws Exception {
        // type the username and password as located from the property file

        try {

            selenium.type(Locators.id, "username", propertyfile.getPropValues(propfile, "staffPortalLogin" + loginType));
            selenium.type(Locators.id, "password", propertyfile.getPropValues(propfile, "staffPortalPassword" + loginType));
            selenium.click(Locators.partialclass, "login-btn");
        }
        catch (Exception e) {
            System.out.println("Already logged in");
        }

        return true;
    }

    public enum SearchTypes {
        policy,
        memberName;
    }

    private enum Screens {
        member,
        policy;
    }

    // for searching by a known policy id
    public void searchByPolicyId(String policyId) throws Exception {
        selenium.type(Locators.id,"id_member_search_term",policyId);
        selenium.pressReturn(Locators.id,"id_member_search_term");
        assert((selenium.getElementText(Locators.partialclass,"tab-summary--active").contains(policyId))) : "Policy not found";
        System.out.println("POLICY FOUND");
    }

    // search for an existing member by name
    public void searchByMemmberName(String memberName, String dob) throws Exception {

        SimpleDateFormat dt = new SimpleDateFormat("yyyy-DD-mm");
        Date date = dt.parse(dob);
        SimpleDateFormat dt1 = new SimpleDateFormat("DD/mm/yyyy");
        dob = (dt1.format(date));

        selenium.type(Locators.id,"id_member_search_term",memberName);
        //selenium.type(Locators.id,"id_date_of_birth", dob); // SEARCH USING DOB CURRENTLY NOT WORKING
        selenium.pressReturn(Locators.id,"id_member_search_term");
        // wait for either single or multiple result
        HashMap<String,Locators> waitElements = new HashMap();
        waitElements.put("search-results-modal",Locators.id);
        waitElements.put("Member details",Locators.linktext);
        selenium.waitForElementListPresent(waitElements,20,0.2);
        // handle multiple result by using date of brith
        if (selenium.checkElementDisplayed(Locators.id,"search-results-modal")) {
            selenium.click(getElementMatchingAttriubte("Date of birth", dob, "label--block"));
        }

        System.out.println(memberName);
        System.out.println(selenium.getElementText(Locators.partialclass,"tab-summary--active"));
        assert((selenium.getElementText(Locators.partialclass,"tab-summary--active").contains(memberName))) : "Member not found";
        selenium.highlight(Locators.partialclass,"tab-summary--active");
        try {
            BromhamStepDefinitions.scenario.embed(selenium.takeScreenshotFullScreenAsByte(), "img/png");
        }
        catch (Exception e) {

        }
    }

    // open a tab in the staff portal (member or policy) and verify that it is selected
    private void openPortalTab(Screens screen, String tabLinkText) throws Exception {
        // check on the correct tab and if not select it
        if (screen.equals(Screens.member)) {
            if (selenium.getElementText(Locators.partialclass,"tab-summary--active").contains("Member summary")==false) {
                selenium.click(Locators.partialLinkDestination, "/member/");
            }
        }
        else if (screen.equals(Screens.policy)) {
            if (!selenium.getElementText(Locators.partialclass, "tab-summary--active").contains("Policy summary")) {
                selenium.click(Locators.partialLinkDestination, "/policy/");
            }
        }
        selenium.click(Locators.linktext,tabLinkText);
        selenium.waitForElementClassPresent(Locators.linktext,tabLinkText,10,100,"bg-turquoise");
    }

    //////////////////////////////////
    // Policy Alternations
    //////////////////////////////////

    // change address of existing member
    public void changeAddress()throws Exception {

        // open the correct page on the staff portal
        openPortalTab(Screens.member,"Member details");

        // open the contact details edit form
        selenium.click(Locators.name,"contactedit");
        selenium.waitForElementPresent(Locators.id,"ContactEdit",10,0.1);

        // new details
        Map<String,String> inputData = getTestScenarioData("change-address","input");
        // input the new address details
        for (Map.Entry<String, String> entry : inputData.entrySet()) {
            setAttributeValue("control-label", entry.getKey(), entry.getValue());
        }
        selenium.type(Locators.id,"id_postcode",inputData.get("Postcode"));

        // use the address lookup
        selenium.click(Locators.id,"js-postcode-lookup");
        selenium.waitForElementPresent(Locators.classname,"address-item-1",10,0.1);
        selenium.click(selenium.getWebElements(Locators.classname,"js-adress-link").get(0));
        selenium.wait(2);

        // submit the change of details
        selenium.click(Locators.attribute,"value='Ok'");
        selenium.waitForElementPresent(Locators.classname,"green",10,0.1);

        System.out.println("==========Finished CHANGE ADDRESS==========");
    }

    public void verifyChangeOfAddress() throws Exception {
        // get the expected output
        Map<String,String> outputData = getTestScenarioData("change-address","output");

        // verify the final bank details
        for (Map.Entry<String, String> entry : outputData.entrySet()) {
            String actualValue = getAttributeValue(entry.getKey(),"form-control-flex__label");
            assert(actualValue.equals(entry.getValue())) : entry.getKey() + " assert failed. Expected: " + entry.getValue() + " Actual: " + actualValue;
        }

        // check the additional address details
        Map<String,String> addressData = getTestScenarioData("change-address","address");
        // verify the final bank details
        for (Map.Entry<String, String> entry : addressData.entrySet()) {
            String actualValue = selenium.getElementText(Locators.partialclass,entry.getKey());
            assert(actualValue.equals(entry.getValue())) : entry.getKey() + " assert failed. Expected: " + entry.getValue() + " Actual: " + actualValue;
        }

        selenium.highlight(Locators.partialclass,"verifiedAddress");
        try {
            BromhamStepDefinitions.scenario.embed(selenium.takeScreenshotFullScreenAsByte(), "img/png");
        }
        catch (Exception e) {

        }
    }

    // change occupation of existing member
    public void changeOccupation()throws Exception {

        System.out.println("==========Start CHANGE OCCUPATION==========");

        openPortalTab(Screens.member,"Member details");
        // get the current salary
        String oldSalary = getAttributeValue("Salary","form-control-flex__label").replace("£","");

        //start the change of occupation
        selenium.click(Locators.id,"submit-id-employmentedit");
        selenium.waitForElementPresent(Locators.id,"EmploymentEdit", 10,0.2);

        // new details
        Map<String,String> inputData = getTestScenarioData("change-occupation","input");
        // input the new bank details
        for (Map.Entry<String, String> entry : inputData.entrySet()) {
            System.out.println(entry.getKey());
            System.out.println(entry.getValue());
            setAttributeValue("control-label", entry.getKey(), entry.getValue());
        }

        selenium.click(Locators.classname, "choices__list--single");
        selenium.type(Locators.classname,"choices__input--cloned",inputData.get("Job title"));
        selenium.pressReturn(Locators.classname,"choices__input--cloned");

        // TODO handle radio for current

        // test the message
        int oldSalaryInt = Integer.parseInt(oldSalary.substring(0,oldSalary.indexOf(".")).replace(",",""));
        int newSalaryInt = Integer.parseInt(inputData.get("Salary"));

        String changeMessage = "";
        if (oldSalaryInt > newSalaryInt) {
            changeMessage = "Advise member that they are now over insured and they need to speak to their adviser";
        }
        else if(oldSalaryInt < newSalaryInt) {
            changeMessage = "Advise member they may be able to increase their cover but they need to speak to their adviser";
        }

        try {
            System.out.println(selenium.getElementText(Locators.classname,"js-salary-message"));
            assert(changeMessage.equals(selenium.getElementText(Locators.classname,"js-salary-message")));
        }

        catch(Exception e) {

        }

        // submit the change and wait
        selenium.click(Locators.attribute,"value='OK'");
        selenium.waitForElementPresent(Locators.classname,"green",10,0.1);

        System.out.println("==========Finished CHANGE OCCUPATION==========");
    }

    public void verifyChangeOfOccupation() throws Exception {
        // get the expected output
        Map<String,String> outputData = getTestScenarioData("change-occupation","output");

        // verify the final bank details
        for (Map.Entry<String, String> entry : outputData.entrySet()) {
            String actualValue = getAttributeValue(entry.getKey(),"form-control-flex__label");
            assert(actualValue.equals(entry.getValue())) : entry.getKey() + " assert failed. Expected: " + entry.getValue() + " Actual: " + actualValue;
        }

        highlightAttribute("Job title","form-control-flex__label");
        try {
            BromhamStepDefinitions.scenario.embed(selenium.takeScreenshotFullScreenAsByte(),"img/png");
        }
        catch (Exception e) {

        }

    }

    // add new bank details for existing member
    public void addNewBankDetails()throws Exception {
        // open the right page in the portal
        openPortalTab(Screens.policy,"Financials");
        selenium.click(Locators.name,"paymentmethodadd");
        selenium.waitForElementPresent(Locators.id,"paymentMethodAdd",10,0.1);

        // new details
        Map<String,String> inputData = getTestScenarioData("new-bank-details","input");
        // input the new bank details
        for (Map.Entry<String, String> entry : inputData.entrySet()) {
            setAttributeValue("label", entry.getKey(), entry.getValue());
        }

        // perform the lookup
        selenium.click(Locators.id,"js-bank-details-lookup");
        selenium.waitForElementPresent(Locators.id,"js-bank-details-answers",10,0.2);

        System.out.println("\"==========Finished ADD NEW BANK==========\"");
    }

    public void verifyBankDetailsAdd() throws Exception {

        // get the expected output
        Map<String,String> outputData = getTestScenarioData("new-bank-details","output");

        // verify the output of the lookup
        for (Map.Entry<String, String> entry : outputData.entrySet()) {
            String actualValue = getAttributeValue(entry.getKey(),"form-control-flex__label");
            assert(actualValue.equals(entry.getValue())) : entry.getKey() + " assert failed. Expected: " + entry.getValue() + " Actual: " + actualValue;
        }

        // submit the form
        selenium.click(Locators.attribute,"value='Add'");
        selenium.waitForElementPresent(Locators.classname,"green",10,0.1);

        // override expected account number
        outputData.put("Account number", "*****"+outputData.get("Account number").substring(5));

        // expand the bank details
        selenium.click(Locators.partialLinkDestination,"#details-pm-1");

        // verify the final bank details
        for (Map.Entry<String, String> entry : outputData.entrySet()) {
            String actualValue = getAttributeValue(entry.getKey(),"label");
            assert(actualValue.equals(entry.getValue())) : entry.getKey() + " assert failed. Expected: " + entry.getValue() + " Actual: " + actualValue;
        }

        selenium.highlight(Locators.id,"paymentMethodEdit");
        try {
            BromhamStepDefinitions.scenario.embed(selenium.takeScreenshotFullScreenAsByte(), "img/png");
        }
        catch (Exception e) {

        }
    }

    // add a note to an existing policy
    public void addNotePolicy()throws Exception {

        openPortalTab(Screens.policy,"Notes");
        selenium.click(Locators.attribute,"value='Add new note'");
        selenium.waitForElementPresent(Locators.id,"noteAdd",10,0.1);

        // new details
        Map<String,String> inputData = getTestScenarioData("add-note","input");
        // input the note details
        for (Map.Entry<String, String> entry : inputData.entrySet()) {
            setAttributeValue("label-narrow", entry.getKey(), entry.getValue());
        }

        // submit the note
        selenium.click(Locators.attribute,"value='Save note'");
        // wait for success mesage to appear
        selenium.waitForElementPresent(Locators.classname,"green",10,0.1);

        System.out.println("==========Finished ADD NOTE==========");
    }

    // check the note is present
    public void verifyPolicyNote() throws Exception {
        // expand the saved note
        selenium.click(Locators.partialLinkDestination,"#details--1");

        // get the expected output
        Map<String,String> outputData = getTestScenarioData("add-note","output");

        // verify the final bank details
        for (Map.Entry<String, String> entry : outputData.entrySet()) {
            String actualValue = getAttributeValue(entry.getKey(),"label--block");
            assert(actualValue.equals(entry.getValue())) : entry.getKey() + " assert failed. Expected: " + entry.getValue() + " Actual: " + actualValue;
        }

        selenium.highlight(selenium.getWebElements(Locators.classname,"row").get(0));

        try {
            BromhamStepDefinitions.scenario.embed(selenium.takeScreenshotFullScreenAsByte(),"img/png");
        }
        catch (Exception e) {

        }
    }

    //////////////////////////////////
    // Policy Servicing
    //////////////////////////////////

    // add a premium holiday for an existing policy
    public void addPremiumHoliday()throws Exception {

        openPortalTab(Screens.policy,"Financials");
        selenium.click(Locators.name,"premiumholidayadd");
        selenium.waitForElementPresent(Locators.id,"PremiumHolidayAdd",10, 0.1);

        // get input data
        Map<String,String> inputData = getTestScenarioData("premium-holiday","input");
        // input the premium holiday
        selenium.selectByText(Locators.partialid,"_month",inputData.get("Month"));
        selenium.selectByText(Locators.partialid,"_year",inputData.get("Year"));
        selenium.type(Locators.attribute,"type='number'",inputData.get("Duration"));

        // submit the form
        selenium.click(Locators.attribute,"value='OK'");
        // wait for confirmation
        selenium.waitForElementPresent(Locators.classname,"green",10,0.1);

        System.out.println("==========Finished ADDING PREMIUM HOLIDAY==========");
    }

    public void verifyPremiumHoliday() throws Exception {
        // get the expected output
        Map<String,String> outputData = getTestScenarioData("premium-holiday","output");

        // verify the final bank details
        for (Map.Entry<String, String> entry : outputData.entrySet()) {
            String actualValue = getAttributeValue(entry.getKey(),"label--block");
            assert(actualValue.equals(entry.getValue())) : entry.getKey() + " assert failed. Expected: " + entry.getValue() + " Actual: " + actualValue;
        }

        selenium.highlight(selenium.getWebElements(Locators.classname,"listings").get(0));
        try {
            BromhamStepDefinitions.scenario.embed(selenium.takeScreenshotFullScreenAsByte(), "img/png");
        }
        catch(Exception e) {

        }
    }

    // adds a premium tranch to an existing premium
    public void addAPremiumTranch() throws Exception {
        openPortalTab(Screens.policy,"Policy Details");
        //selenium.click(Locators.classname, "js-open-dropdown");
        selenium.click(Locators.name, "premiumbenefitedit");
        selenium.click(Locators.name, "new-iptranche");
        selenium.waitForElementPresent(Locators.name,"save-iptranche",10,0.2);

        // new details
        Map<String,String> inputData = getTestScenarioData("add-tranche","immediate","input");
        selenium.click(Locators.cssSelector,"div.choices__item.choices__item--selectable");
        selenium.type(Locators.cssSelector,"input.choices__input.choices__input--cloned",inputData.get("Select financial adviser"));
        selenium.pressReturn(Locators.cssSelector,"input.choices__input.choices__input--cloned");
        // input the note details
        for (Map.Entry<String, String> entry : inputData.entrySet()) {
            setAttributeValue("control-label", entry.getKey(), entry.getValue());
        }
        selenium.moveFocus();
        List<WebElement> list = selenium.getWebElements(Locators.name,"save-iptranche");
        // FORCE BUTTON CLICK
        for (int i = 0; i < list.size(); i++) {
            selenium.click(list.get(i));
        }
        selenium.waitForElementPresent(Locators.classname,"green",120,0.2);
    }

    // adds a premium tranch to an existing premium
    public void clonePremiumTranch() throws Exception {
        openPortalTab(Screens.policy,"Policy Details");
        selenium.click(Locators.name, "premiumbenefitedit");
        selenium.click(Locators.partialLinkDestination, "details-trancheScroller-1");
        selenium.waitForElementPresent(Locators.name, "clone-1", 10, 0.2);
        selenium.click(Locators.name, "clone-1");
        selenium.waitForElementClassPresent(Locators.linktext,"Policy Details",10,100,"bg-turquoise");
        selenium.click(Locators.name, "premiumbenefitedit");
        selenium.click(Locators.partialLinkDestination,"details-trancheScroller-2");
        selenium.waitForElementPresent(Locators.name, "rowedit-2", 10, 0.2);
        selenium.click(Locators.name, "rowedit-2");
        selenium.waitForElementPresent(Locators.name, "update-2", 10, 0.2);
        setAttributeValue("control-label","Coverage Amount*","500");
        selenium.waitForElementPresent(Locators.name,"save-iptranche",10,0.2);

        selenium.waitForElementPresent(Locators.classname,"green",120,0.2);
    }

    // cancel a policy
    public void cancelPolicy(String cancelReason, boolean refund)throws Exception {
        openPortalTab(Screens.policy,"Policy Details");
        selenium.click(Locators.classname,"action-dropdown__main");
        selenium.click(Locators.id,"submit-id-policycancel");
        selenium.waitForElementPresent(Locators.id,"submit-id-back-button",60,0.2);

        // get input data
        Map<String,String> inputData = getTestScenarioData("cancel-policy","input");
        setAttributeValue("control-label","Cancelled date*", inputData.get("Cancelled date"));
        setAttributeValue("label-narrow","Note",inputData.get("Note"));
        if (!cancelReason.equals("")) {
            setAttributeValue("label-narrow","Note type",cancelReason);
        }
        else {
            setAttributeValue("label-narrow","Note type",inputData.get("Note type"));
        }
        if (refund) {
            refundPolicy();
        }
        else {
            // submit the form
            selenium.click(Locators.attribute, "value='Cancel policy'");
            selenium.waitForElementPresent(Locators.classname,"green",10,0.1);
        }

        System.out.println("==========Finished CANCEL POLICY==========");
    }

    // verify policy canceled
    public void verfiyPolicyStatus(String status)throws Exception {

        // check the status after cancellation
        String policyStatus = getAttributeValue("Policy status", "form-control-flex__label");
        assert(policyStatus.equals(status)) : "Policy status = " + policyStatus;

        highlightAttribute("Policy status", "form-control-flex__label");
        try {
            BromhamStepDefinitions.scenario.embed(selenium.takeScreenshotFullScreenAsByte(), "img/png");
        }
        catch(Exception e) {

        }
    }

    // cancel and refund a policy
    private void refundPolicy()throws Exception {

        selenium.click(Locators.attribute, "value='Refund'");
        selenium.waitForElementPresent(Locators.classname,"green",10,0.1);

        Map<String,String> inputData = getTestScenarioData("cancel-policy","input-refund");
        setAttributeValue("control-label","Amount*",inputData.get("Amount*"));
        setAttributeValue("control-label","Status*",inputData.get("Status*"));
        setAttributeValue("label-narrow","Note type",inputData.get("Note type"));
        setAttributeValue("label-narrow","Note",inputData.get("Note"));

        // submit the refund
        selenium.click(Locators.attribute,"value='OK'");
        selenium.waitForElementPresent(Locators.classname,"green",10,0.1);

        System.out.println("==========Finished CANCEL AND REFUND==========");
    }

    public void verifyCancellationRefund() throws Exception {
        openPortalTab(Screens.policy,"Financials");

        // get the expected output
        Map<String,String> outputData = getTestScenarioData("cancel-policy","output-refund");
        // verify the final bank details
        for (Map.Entry<String, String> entry : outputData.entrySet()) {
            String actualValue = getAttributeValueFromParent(entry.getKey(),"label",selenium.getWebElement(Locators.id,"id_row-id-0"));
            assert(actualValue.equals(entry.getValue())) : entry.getKey() + " assert failed. Expected: " + entry.getValue() + " Actual: " + actualValue;
        }

        selenium.highlight(Locators.id,"id_row-id-0");
        try {
            BromhamStepDefinitions.scenario.embed(selenium.takeScreenshotFullScreenAsByte(), "img/png");
        }
        catch (Exception e) {

        }
    }

    // reinstate a policy
    public void reinstateAPolicy(String reinstateReason)throws Exception {
        // cancel the policy for cooling off

        selenium.click(Locators.classname,"action-dropdown__main");
        selenium.click(Locators.id,"submit-id-policyreinstate");
        selenium.waitForElementPresent(Locators.id,"submit-id-back-button",10,0.2);

        // get input data
        Map<String,String> inputData = getTestScenarioData("cancel-policy","input-reinstate");
        setAttributeValue("control-label","Start date*", inputData.get("Start date"));
        setAttributeValue("label-narrow","Note",inputData.get("Note"));
        if (reinstateReason.equals("") == false) {
            setAttributeValue("label-narrow","Note type",reinstateReason);
        }
        else {
            setAttributeValue("label-narrow","Note type",inputData.get("Note type"));
        }

        // submit the form
        selenium.click(Locators.attribute,"value='Reinstate'");
        selenium.waitForElementPresent(Locators.classname,"green",10,0.1);

        System.out.println("==========Finished CANCEL AND REINSTATE==========");
    }

    // upload a document
    public void uploadADocumentToPolicy() throws Exception {

        openPortalTab(Screens.policy,"Documents");
        selenium.click(Locators.id,"submit-id-documentadd");

        // get data for form and input
        Map<String,String> inputData = getTestScenarioData("add-document","input");
        uploadDocument(inputData);
        System.out.println("==========Finished UPLOAD OF DOCUMENT==========");
    }

    private void uploadDocument(Map<String,String> inputData) throws Exception {
        // input the document details
        for (Map.Entry<String, String> entry : inputData.entrySet()) {
            setAttributeValue("control-label", entry.getKey(), entry.getValue());
        }

        // set the path to a document to upload
        //selenium.click(Locators.id,"id_document");
        String currentDir = System.getProperty("user.dir");
        //String filepath = currentDir+"\\src\\test\\resources\\com\\pancentric\\bromham\\documents\\cucumber-results.pdf";
        String filepath = currentDir+"/src/test/resources/com/pancentric/bromham/documents/cucumber-results.pdf";
        selenium.handleUploadEvent(filepath,"id_document");

        // submit the form
        selenium.click(Locators.attribute,"value='Ok'");
        selenium.waitForElementPresent(Locators.classname,"green",10,0.1);
    }

    // verify the policy upload
    public void verifyPolicyDocumentUpload() throws Exception {
        // expand the saved note
        selenium.click(Locators.partialLinkDestination,"#details--");

        // get the expected output
        Map<String,String> outputData = getTestScenarioData("add-document","output");

        // verify the final bank details
        for (Map.Entry<String, String> entry : outputData.entrySet()) {
            String actualValue = getAttributeValue(entry.getKey(),"label--block");
            assert(actualValue.equals(entry.getValue())) : entry.getKey() + " assert failed. Expected: " + entry.getValue() + " Actual: " + actualValue;
        }

        selenium.highlight(Locators.tagname,"fieldset");
        try {
            BromhamStepDefinitions.scenario.embed(selenium.takeScreenshotFullScreenAsByte(), "img/png");
        }
        catch (Exception e) {

        }
    }

    //////////////////////////////////
    // Claims
    //////////////////////////////////

    // Create a new claim
    public void createANewClaim() throws Exception {

        weeklyBenefit = getAttributeValue("Weekly Benefit","lead");

        // go to claims page
        openPortalTab(Screens.policy,"Claims");

        // move through first screen
        selenium.selectByText(Locators.name,"claims-decision","Proceed");
        selenium.wait(1);
        selenium.click(Locators.attribute,"value='Start Pre-Admittance'");
        waitForClaimStatus("Pre admittance",10,0.2);

        // add a medical condition
        Map<String,String> conditionDetails = addMedicalCondition();

        setAbsenceDates(conditionDetails.get("Start Date*"), conditionDetails.get("End Date*"));

        // proceed through pre-admittance
        selenium.selectByText(Locators.name,"claims-decision","Proceed");

        // create the claim form
        selenium.click(Locators.attribute,"value='Create form'");
        selenium.waitForElementPresent(Locators.name,"back-button",10,0.2);
        selenium.switchFrame(Locators.classname,"cke_wysiwyg_frame");
        selenium.type(Locators.classname,"cke_editable","TEST");
        selenium.selectMainFrame();
        selenium.click(Locators.attribute,"value='Generate claim form'");

        selenium.waitForElementPresent(Locators.xpath,"//*[@id=\"id-exampleForm\"]/div[1]",10,0.5);

        waitForClaimStatus("New claim",10,0.2);

        selectedRow = 0;
        // add completed claim form
        selenium.click(Locators.name,"documentadd");
        selenium.waitForElementPresent(Locators.id,"id_document",10,0.2);
        // get data for form and input
        Map<String,String> inputData = getTestScenarioData("add-document","add-document-claim-form","input");
        uploadDocument(inputData);

        setAttributeValue("label--inline","Medically Valid","");
        setAttributeValue("label--inline","Financially Valid","");
        setAttributeValue("label-narrow","Note","TEST");

        selenium.click(Locators.attribute,"value='Accept and set up payments'");
        waitForClaimStatus("Payments & Reviews",10,0.2);

        System.out.println("==========Finished CREATE CLAIM==========");
    }

    // verify claim benefit payments are setup
    public void verifyClaimPaymentsSetup() throws Exception {
        assert(getClaimSubStatus().contains("Valid"));
        assert(getClaimSubStatus().contains("In payment"));
        selenium.click(Locators.classname,"js-open-dropdown");
        selenium.click(Locators.name, "benefitpaymentschedule");
        String secondPayment = selenium.getElementText(Locators.xpath,"//*[@id=\"policy_data\"]/div/div[3]/table/tbody/tr[3]/td[2]");
        assert(secondPayment.contains(weeklyBenefit));
    }

    private Map<String,String> addMedicalCondition() throws Exception {
        // add new medical condition
        selenium.click(Locators.name,"new-medicalconditionasset");
        selenium.waitForElementPresent(Locators.id,"js-condition-lookup",10,0.2);

        // new details
        Map<String,String> inputData = getTestScenarioData("medical-condition","input");
        // input the condition details
        for (Map.Entry<String, String> entry : inputData.entrySet()) {
            setAttributeValue("control-label", entry.getKey(), entry.getValue());
        }

        // get the expected output
        Map<String,String> outputData = getTestScenarioData("medical-condition","output");

        selenium.click(Locators.id,"js-condition-lookup");
        selenium.waitForElementPresent(Locators.attribute,"data-condition-description='"+outputData.get("Condition")+"'",10,0.2);

        //selenium.moveFocus();
        selenium.click(Locators.partialname,"save-medical-condition");
        selenium.waitForElementPresent(Locators.classname,"green",10,0.1);

        return inputData;
    }

    private void setAbsenceDates(String startDate, String endDate) throws Exception {
        selenium.scrollToElement(Locators.name,"new-medicalconditionasset");
        setAttributeValue("control-label","First day of absence",startDate);
        //selenium.moveFocus();
        setAttributeValue("control-label","Expected last day of absence",endDate);
        //selenium.moveFocus();
        selenium.click(Locators.attribute,"value='Save dates'");
        selenium.waitForElementPresent(Locators.classname,"green",10,0.1);
    }

    private void waitForClaimStatus(String waitForStatus, int timeout, double interval) throws Exception {

        selenium.turnOffImplicitWaits();
        long end = System.currentTimeMillis() + (timeout * 1000);

        timer:
        while (System.currentTimeMillis() < end) {

            try {
                selenium.getWebElementNoHandling(Locators.xpath, "//*[@id=\"id-exampleForm\"]/div[1]");

                selenium.waitForElementPresent(Locators.xpath,"//*[@id=\"id-exampleForm\"]/div[1]",10,0.5);
                WebElement rows = selenium.getWebElement(Locators.xpath, "//*[@id=\"id-exampleForm\"]/div[1]");
                List<WebElement> rowItems = selenium.getChildElementsXpath(rows,".//div");
                Iterator<WebElement> iterator = rowItems.iterator();
                loop:
                while (iterator.hasNext()) {
                    WebElement row = iterator.next();
                    if (selenium.getElementClass(row).contains("bg-turquoise")) {
                        if(selenium.getElementText(row).equals(waitForStatus)) {
                            System.out.println("NOW FOUND STATUS: " + selenium.getElementText(row));
                            System.out.println("SUB STATUS: " + getClaimSubStatus());
                            //break timer;
                            break timer;
                        }

                        else {
                            selenium.wait(interval);
                            System.out.println("status currently " + selenium.getElementText(row));
                        }
                    }
                }
            }

            catch (org.openqa.selenium.NoSuchElementException e) {
                System.out.println("element not found this time");
                selenium.wait(interval);
                System.out.println("waited");
            }
        }
        selenium.turnOnImplicitWaits();
    }

    private String getClaimSubStatus() throws Exception {
        return selenium.getElementText(Locators.cssSelector, "a.link--block.tab-summary--active > div");
    }

    // a method to populate a form section with data from
    private Map<String,String> getTestScenarioData(String testScenario, String inputOutput) throws Exception {
        return getTestScenarioData(testScenario,testScenario,inputOutput);
    }

    // a method to populate a form section with data from
    private Map<String,String> getTestScenarioData(String sheetname, String testScenario, String inputOutput) throws Exception {

        // type the username and password as located from the property file
        String datapath = propertyfile.getPropValues(propfile, "quoteApplyDataPath");

        // TODO refactor excel fetcher
        ExcelFetcher memberData = new ExcelFetcher();
        determineSelectedRow(memberData,datapath,sheetname,testScenario);

        // get the details from the selected row
        Map<String,String> formInputData = memberData.getARandomRecordMap(datapath,sheetname,inputOutput,selectedRow);

        return formInputData;
    }

    // choose a matching row for the test scenario
    private void determineSelectedRow(ExcelFetcher memberData, String datapath, String sheetname, String testScenario) throws Exception {
        if (selectedRow == 0) {
            // get a row matching the scenario
            Random rand = new Random();
            List<Integer> matchingRows = memberData.matchingRows(datapath,sheetname,testScenario, 0);
            selectedRow = matchingRows.get(rand.nextInt(matchingRows.size()));
            if (testScenario.equals("")) {
                // get any random selected row
                selectedRow = selenium.randomNumberBetweenTwoPoints(2,memberData.maxRows(datapath,sheetname));
            }
        }
    }

    // for getting an attribute value from the member page for a given label - works for policy?
    private String getAttributeValue(String attributeName, String className) throws Exception {
        List<WebElement> attributeLabels = selenium.getWebElements(Locators.classname, className);
        String returnText = "";
        element_loop:
        for (int i = 0; i < attributeLabels.size(); i++) {
            if (selenium.getElementText(attributeLabels.get(i)).equals(attributeName)) {
                System.out.println(selenium.getElementText(attributeLabels.get(i)));
                returnText = selenium.getElementText(selenium.getSiblingElement(attributeLabels.get(i),"::*"));
                break element_loop;
            }
        }
        return returnText;
    }

    // for getting an attribute value from the member page for a given label - works for policy?
    private void highlightAttribute(String attributeName, String className) throws Exception {
        List<WebElement> attributeLabels = selenium.getWebElements(Locators.classname, className);
        element_loop:
        for (int i = 0; i < attributeLabels.size(); i++) {
            if (selenium.getElementText(attributeLabels.get(i)).equals(attributeName)) {
                System.out.println(selenium.getElementText(attributeLabels.get(i)));
                selenium.highlight((selenium.getSiblingElement(attributeLabels.get(i),"::*")));
                break element_loop;
            }
        }
    }

    // get element
    private WebElement getElementMatchingAttriubte(String attributeName, String attributeValue, String className) throws Exception {
        List<WebElement> attributeLabels = selenium.getWebElements(Locators.classname, className);
        WebElement myElement = null;
        element_loop:
        for (int i = 0; i < attributeLabels.size(); i++) {
            if (selenium.getElementText(attributeLabels.get(i)).equals(attributeName)) {
                System.out.println(selenium.getElementText(attributeLabels.get(i)));
                if (selenium.getElementText(selenium.getSiblingElement(attributeLabels.get(i),"::*")).equals(attributeValue)) {
                    myElement = selenium.getSiblingElement(attributeLabels.get(i),"::*");
                    break element_loop;
                }
            }
        }
        return myElement;
    }

    // for getting an attribute value from the member page for a given label - works for policy?
    private String getAttributeValueFromParent(String attributeName, String className, WebElement parent) throws Exception {

        List<WebElement> attributeLabels = selenium.getChildElementsXpath(parent,".//*[contains(@class, '" + className + "')]");
        System.out.println(attributeLabels.size());
        String returnText = "";
        element_loop:
        for (int i = 0; i < attributeLabels.size(); i++) {
            if (selenium.getElementText(attributeLabels.get(i)).equals(attributeName)) {
                System.out.println(selenium.getElementText(attributeLabels.get(i)));
                returnText = selenium.getElementText(selenium.getSiblingElement(attributeLabels.get(i),"::*"));
                break element_loop;
            }
        }
        return returnText;
    }

    // for setting the value of an attribute by attribute name
    private void setAttributeValue(String classname, String attributeName, String value) throws Exception {
        System.out.println(attributeName);
        List<WebElement> attributeLabels = selenium.getWebElements(Locators.classname, classname);
        element_loop:
        for (int i = 0; i < attributeLabels.size(); i++) {
            if (selenium.getElementText(attributeLabels.get(i)).equals(attributeName)) {
                System.out.println("FOUND" + attributeName);
                WebElement nextSibling = null;
                try {
                    if (classname.contains("inline")) {
                        System.out.println("IN INLINE");
                        nextSibling = selenium.getPreviousSiblingElement(attributeLabels.get(i),"::*");
                    }
                    else {
                        nextSibling = selenium.getSiblingElement(attributeLabels.get(i),"::*");
                    }
                }
                // handling for cases where form element is a sibling of the parent
                catch (Exception e) {
                    nextSibling = selenium.getParentElement(attributeLabels.get(i));
                    nextSibling = selenium.getSiblingElement(nextSibling,"::*");
                }
                String outer = selenium.getElementTagName(nextSibling);
                System.out.println(outer);
                if (outer.contains("div")) {
                    nextSibling = selenium.getChildElements(nextSibling).get(0);
                    outer = selenium.getElementTagName(nextSibling);
//                    if (outer.contains("div")) {
//                        nextSibling = selenium.getChildElements(nextSibling).get(0);
//                        outer = selenium.getElementTagName(nextSibling);
//                    }
                }
                System.out.println(outer);

                if (outer.equals("select")) {
                    selenium.selectByText(nextSibling,value);
                }
                else if (outer.equals("textarea")) {
                    selenium.type(nextSibling, value);
                }
                else if (outer.contains("input")) {

                    if (selenium.getElementOuterHTML(nextSibling).contains("checkbox")) {
                        System.out.println("IN CHECKBOX");
                        selenium.click(nextSibling);
                    }
                    else {
                        selenium.type(nextSibling, value);
                    }

                }
                // if radio //TODO needs improvment
                else if (outer.contains("label")) {
                    if (nextSibling.getText().contains(value)) {
                        selenium.click(nextSibling);
                    }
                    else {
                        nextSibling = selenium.getSiblingElement(nextSibling,"::*");
                        if (nextSibling.getText().contains(value)) {
                            selenium.click(nextSibling);
                        }
                    }
                }

                break element_loop;
            }
        }
    }
}
