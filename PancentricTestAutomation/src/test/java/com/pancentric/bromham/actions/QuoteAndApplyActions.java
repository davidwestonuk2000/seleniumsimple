package com.pancentric.bromham.actions;

import com.pancentric.bromham.BromhamStepDefinitions;
import com.pancentric.utilities.ExcelFetcher;
import com.pancentric.utilities.PropertyFetcher;
import common.AssertionFailed;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import io.restassured.specification.RequestSpecification;
import org.apache.commons.io.IOUtils;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.openqa.selenium.WebDriver;
import com.pancentric.utilities.SeleniumWebdriver.Locators;
import com.pancentric.utilities.SeleniumWebdriver;
import org.openqa.selenium.WebElement;
import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.responseSpecification;

import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
 import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

public class QuoteAndApplyActions {
    //this is our selenium instance
    private SeleniumWebdriver selenium;
    //this is our selenium webdriver controlling our browsers
    private WebDriver driver;
    private int selectedRow = 0;
    private String policyScenario = "";
    private  String feAdvisorUrl;
    private PropertyFetcher propertyfile;
    private String propfile;
    private Map<String,String> memberQuoteDetailsFull = new HashMap<>();
    private String bearer = "";

    //our constructor getting our selenium instance, and the driver to be used

    public QuoteAndApplyActions(SeleniumWebdriver selenium) throws Exception {

        this.selenium = selenium;
        //this.driver = selenium.getDriver();

        //creates our selenium instance, and starts the driver
        propertyfile = new PropertyFetcher();
        // locate the appropriate prop file
        propfile = System.getProperty("project") + "/properties/"+ System.getProperty("environment");
        feAdvisorUrl = propertyfile.getPropValues(propfile, "feAdvisorUrl");
    }

    // open the FA adviser url
    public void openFaAdivisoeUrl() throws Exception {
        selenium.loadPageSameWindow(feAdvisorUrl);
    }

    // a method for completing the quote process
    public void completeQuote(String polScenario) throws Exception {
        this.policyScenario = polScenario;
        // load the advisor url
        openFaAdivisoeUrl();
        // open quote and apply
        selenium.click(Locators.linktext, "Get a quote");
        selenium.waitForElementTextPresent(Locators.cssSelector,"h1","Get a quote",30,100);
        // close cookie message
        selenium.click(Locators.linktext,"Close");
        selenium.wait(5);

        // populate quote details
        // TODO workaround with quote type
        populateMemberDetails("quote");

        // submit quote
        selenium.click(Locators.partialclass, "submit-quote");

        // get the quote value
        BromhamStepDefinitions.scenario.write(selenium.getElementText(Locators.classname,"price-bubble__container"));
        BromhamStepDefinitions.scenario.embed(selenium.takeScreenshotFullScreenAsByte(),"img/png");
    }

    // for completing the details of an application
    public Map<String,String> completeApplication() throws Exception {

        selenium.click(Locators.attribute, "value='Apply now'");

        // enter some application details
        populateMemberDetails("apply-email");
        selenium.moveFocus();
        populateMemberDetails("apply");
        // override name details
        Map<String,String> name = nameGenerator("male","england");
        selenium.type(Locators.partialid,"firstname",name.get("name"));
        selenium.type(Locators.partialid,"lastname",name.get("surname"));
        memberQuoteDetailsFull.put("name",name.get("name"));
        memberQuoteDetailsFull.put("surname",name.get("surname"));

        selenium.click(Locators.attribute, "value='Apply'");

        // LOGIN TO ADVISOR PORTAL
        assert(login("Normal")==true);

        // AGREEMENT SECTION
        populateMemberDetails("agreement");
        selenium.click(Locators.attribute, "value='Agree'");

        /// CONTACT SECTION
        populateMemberDetails("contact");
        // use the find address tool
        selenium.click(Locators.id,"postcode-lookup");
        selenium.wait(1);
        selenium.click(Locators.partialclass,"p- address-link");
        selenium.wait(1);
        // submit the contact details
        selenium.click(Locators.attribute, "value='Next'");

        // OCCUPATION SECTION
        populateMemberDetails("occupational");
        selenium.click(Locators.attribute, "value='Next'");

        // EARNINGS SECTION
        populateMemberDetails("earnings");
        selenium.click(Locators.attribute, "value='Next'");

        // MEDICAL SECTION
        populateMemberDetails("medical");
        selenium.click(Locators.attribute, "value='Next'");

        // MORE MEDICAL SECTION
        populateMemberDetails("medical_more");
        selenium.moveFocus();
        selenium.click(Locators.attribute, "value='Next'");

        // TODO this might not be available
        // START SIMPLIFIED APPLICATION;
        selenium.click(Locators.attribute, "value='Start Simplified application'");

        populateMemberDetails("simplified");
        selenium.click(Locators.id,"id_confirm_definition");
        selenium.click(Locators.id,"id_confirm_activities");
        selenium.click(Locators.id,"id_confirm_injury_claim");
        selenium.click(Locators.attribute, "value='Next'");

        // Start immediately
        selenium.click(Locators.attribute,"value='asap'");
        selenium.click(Locators.attribute, "value='Next'");

        // POPULATE PAYMENT DETAILS
        populateMemberDetails("payment");
        selenium.click(Locators.attribute, "value='Next'");

        // POPULATE DECLARATION DETAILS
        populateMemberDetails("declaration");
        selenium.click(Locators.attribute, "value='Next'");

        // POPULATE COMMISSION DETAILS
        populateMemberDetails("commission");
        selenium.click(Locators.attribute, "value='Submit'");

        // wait for the application to load
        //selenium.waitForElementPresent(Locators.partialclass,"loader-container",10,0.1);
        // TODO turn into positive wait
        selenium.waitForElementNotPresent(Locators.partialclass,"loader-container",120,0.1);

        return memberQuoteDetailsFull;
    }

    // for logging in to the advisers portal
    public boolean login(String loginType) throws Exception {
        System.out.println(propertyfile.getPropValues(propfile, "faLogin"+loginType));
        // type the username and password as located from the property file
        selenium.type(Locators.id, "input-email", propertyfile.getPropValues(propfile, "faLogin"+loginType));
        selenium.type(Locators.id, "input-password", propertyfile.getPropValues(propfile, "faPassword"+loginType));
        String pin = propertyfile.getPropValues(propfile, "faPin"+loginType);
        String faName = propertyfile.getPropValues(propfile, "faName"+loginType);
        // calculate the pin number elements to enter
        List<WebElement> pinRequest = selenium.getWebElements(Locators.partialid,"input-pin-");
        for (int i = 0; i < pinRequest.size(); i++) {
            int pinId = Integer.parseInt(selenium.getAttributeValue(pinRequest.get(i),"name").replace("pin_",""));
            String pinSequence = pin.substring(pinId-1,pinId);
            selenium.type(pinRequest.get(i), pinSequence);
        }
        selenium.click(Locators.id, "submitBtn");

        boolean loginStatus = false;
        System.out.println(selenium.getElementText(Locators.partialclass,"greeting"));
        if (selenium.checkElementText(Locators.partialclass,"greeting","Hello " + faName)) {
            loginStatus = true;
        }

        return loginStatus;
    }

    // a method to populate a form section with data from
    private void populateMemberDetails(String recordType) throws Exception {

        // type the username and password as located from the property file
        String datapath = propertyfile.getPropValues(propfile, "quoteApplyDataPath");
        String sheetname = propertyfile.getPropValues(propfile, "quoteApplyDataSheet");

        // TODO refactor excel fetcher
        ExcelFetcher memberData = new ExcelFetcher();
        determineSelectedRow(memberData,datapath,sheetname);

        if (memberQuoteDetailsFull.size() < 1) {
            memberQuoteDetailsFull = memberData.getARandomRecordFull(datapath,sheetname,selectedRow);
        }

        // get the details from the selected row
        List<Map<String,String>> memberQuoteDetails = memberData.getARandomRecord(datapath,sheetname,recordType,selectedRow);

        // loop the returned details
        for (int i = 0; i < memberQuoteDetails.size(); i++) {
            System.out.println(memberQuoteDetails.get(i).get("name"));
            System.out.println(memberQuoteDetails.get(i).get("value"));
            String outerHTML = (selenium.getElementOuterHTML(Locators.partialid,memberQuoteDetails.get(i).get("name")));

            // if the field is a text box...
            if (outerHTML.contains("form__input")) {
                selenium.type(Locators.cssSelector, "input[id*="+memberQuoteDetails.get(i).get("name")+"]", memberQuoteDetails.get(i).get("value"));
            }
            // if the field is a radio...
            else if (outerHTML.contains("radio")) {
                selenium.click(Locators.cssSelector,"input[id*=\""+memberQuoteDetails.get(i).get("name")+"\"][value=\""+memberQuoteDetails.get(i).get("value")+"\"]");
            }
            // if the field is a special select
            else if (outerHTML.contains("select2")){
                selenium.click(Locators.partialid,memberQuoteDetails.get(i).get("name"));
                selenium.wait(1);
                selenium.type(Locators.partialclass,"select2-search__field",memberQuoteDetails.get(i).get("value"));
                selenium.wait(1);
                selenium.click(Locators.partialclass,"option--highlighted");
            }
            // if the field is a select
            else if (outerHTML.contains("select")){
                try {
                    System.out.println("IN SELECT");
                    String attribute = (memberQuoteDetails.get(i).get("name"));
                    System.out.println(memberQuoteDetails.get(i).get("value"));
                    selenium.selectByText(Locators.id,"id_"+memberQuoteDetails.get(i).get("name"),memberQuoteDetails.get(i).get("value"));
                    if (attribute.equals("product")) {
                        selenium.wait(5);
                    }
                }
                catch (Exception e) {
                    System.out.println("IN INDEX EXCEPTION");
                    System.out.println(e.getMessage());
                    System.out.println(e.getStackTrace());
                    selenium.selectByIndex(Locators.id,"id_"+memberQuoteDetails.get(i).get("name"),0);
                }
            }
            else if (outerHTML.contains("checkbox")){
                selenium.click(Locators.cssSelector,"input[id*=\""+memberQuoteDetails.get(i).get("name")+"\"][value=\""+memberQuoteDetails.get(i).get("value")+"\"]");
            }
            else {
                System.out.println("NO MATCH");
            }
        }
    }

    // choose a matching row for the scenario
    private void determineSelectedRow(ExcelFetcher memberData, String datapath, String sheetname) throws Exception {
        if (selectedRow == 0) {
            // get a row matching the scenario

            Random rand = new Random();
            List<Integer> matchingRows = memberData.matchingRows(datapath,sheetname,policyScenario, 0);
            selectedRow = matchingRows.get(rand.nextInt(matchingRows.size()));
            if (policyScenario.equals("")) {
                // get any random selected row
                selectedRow = selenium.randomNumberBetweenTwoPoints(2,memberData.maxRows(datapath,sheetname));
            }
        }
    }

    // TODO dont really need this
    private List<Integer> getAllMatchingRows(ExcelFetcher memberData, String datapath, String sheetname) throws Exception {
         return memberData.matchingRows(datapath,sheetname,policyScenario, 0);
    }

    public void createManyPolicies(String polScenario) throws Exception {

        this.policyScenario = polScenario;
        // type the username and password as located from the property file
        String datapath = propertyfile.getPropValues(propfile, "quoteApplyDataPath");
        String sheetname = propertyfile.getPropValues(propfile, "quoteApplyDataSheet");
        ExcelFetcher memberData = new ExcelFetcher();
        List<Integer> rows = memberData.matchingRows(datapath,sheetname,policyScenario, 0);
        List<String> policyIds = new ArrayList<>();
        for (int i = 0; i < rows.size(); i++) {
            selectedRow = rows.get(i);
            createPolicyRest(policyScenario);
            policyIds.add(memberQuoteDetailsFull.get("policyNumber"));
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = new Date();
            try {
                setupPaymentHistory(memberQuoteDetailsFull.get("policyId"), memberQuoteDetailsFull.get("start_date"), dateFormat.format(date));
            }
            catch (AssertionFailed e) {
                System.out.println("FAILED TO CREATE PAYMENT HISTORY");
            }
        }
        for (int j = 0; j < policyIds.size(); j++) {
            System.out.println(policyIds.get(j));
        }

    }

    // for creating a new policy via ou REST call
    public Map<String,String> createPolicyRest(String polScenario) throws Exception {
        this.policyScenario = polScenario;
        // type the username and password as located from the property file
        String datapath = propertyfile.getPropValues(propfile, "quoteApplyDataPath");
        String sheetname = propertyfile.getPropValues(propfile, "quoteApplyDataSheet");

        ExcelFetcher memberData = new ExcelFetcher();

        determineSelectedRow(memberData,datapath,sheetname);
        // get the details from the selected row
        memberQuoteDetailsFull = memberData.getARandomRecordFull(datapath,sheetname,selectedRow);

        // get a random name
        Map<String,String> name = nameGenerator(memberQuoteDetailsFull.get("gender_"),"england");
        memberQuoteDetailsFull.put("name",name.get("name"));
        memberQuoteDetailsFull.put("surname",name.get("surname"));

        // overwrite email
        memberQuoteDetailsFull.put("client_email",(name.get("name") + "." + name.get("surname") + "@test.com"));

        if(memberQuoteDetailsFull.get("quote_type").equals("B")) {
            memberQuoteDetailsFull.put("quote_type","Benefit Led Quote");
        }
        else if ((memberQuoteDetailsFull.get("quote_type").equals("P"))) {
            memberQuoteDetailsFull.put("quote_type","Premium Quote");
        }

        // build the json object
        String data = "{" +
                "  \"annual_income\": \""+memberQuoteDetailsFull.get("annual_income")+"\"," +
                "  \"employed_earnings\": \""+memberQuoteDetailsFull.get("annual_income")+"\"," +
                "  \"quote_type\":  \""+memberQuoteDetailsFull.get("quote_type")+"\"," +
                "  \"quote_type_frequency\": \""+memberQuoteDetailsFull.get("quote_type_frequency")+"\"," +
                "  \""+memberQuoteDetailsFull.get("quote_type_frequency").toLowerCase()+"_benefit\":  \""+memberQuoteDetailsFull.get(memberQuoteDetailsFull.get("quote_type_frequency").toLowerCase()+"_benefit")+"\"," +
                "  \"date_of_birth\": \""+memberQuoteDetailsFull.get("date_of_birth")+"\"," +
                "  \"retirement_age\":  \""+memberQuoteDetailsFull.get("retirement_age")+"\"," +
                "  \"age_retirement_age_differance\": \""+memberQuoteDetailsFull.get("age_retirement_age_differance")+"\"," +
                "  \"deferred_period\":  \""+memberQuoteDetailsFull.get("deferred_period")+"\"," +
                "  \"claim_period\":  \""+memberQuoteDetailsFull.get("claim_period")+"\"," +
                "  \"inflation\":  \""+memberQuoteDetailsFull.get("inflation").replace("Yes","true").replace("No","false")+"\"," +
                "  \"commission_required\":  \""+memberQuoteDetailsFull.get("commission_required")+"\"," +
                "  \"fa_fca_number\": \"1234\"," +
                "  \"fa_irn\": \"123\"," +
                "  \"fa_client_network\": \"12345\"," +
                "  \"fa_client_network_name\": \"PPP\"," +
                "  \"fa_credit_day\": \"1\"," +
                "  \"fa_bankacc_name\": \"Barclays Bank\"," +
                "  \"fa_bankacc_branch_name\": \"Harrogate\"," +
                "  \"fa_bankacc_number\": \"55556666\"," +
                "  \"fa_bankacc_sort_1\": \"55\"," +
                "  \"fa_bankacc_sort_2\": \"66\"," +
                "  \"fa_bankacc_sort_3\": \"77\"," +
                "  \"fa_title\": \"Mr\"," +
                "  \"fa_firstname\": \"Peter\"," +
                "  \"fa_lastname\": \"Parker\"," +
                "  \"fa_telephone\": \"01234 12341234\"," +
                "  \"fa_email\": \"peter.parker@ouwmail.co.uk\"," +
                "  \"fa_company_name\":\"FAL Limited\"," +
                "  \"fa_house_no_or_name\": \"123 Long Street\"," +
                "  \"fa_town\": \"Whitesands\"," +
                "  \"fa_county\": \"Leeds\"," +
                "  \"fa_country\": \"United Kingdom\"," +
                "  \"fa_postcode\": \"LE7 9NG\"," +
                "  \"fa_start_date\": \"2010-01-01\"," +
                "  \"title\":  \""+memberQuoteDetailsFull.get("title")+"\"," +
                "  \"firstname\": \""+memberQuoteDetailsFull.get("name")+"\"," +
                "  \"lastname\": \""+memberQuoteDetailsFull.get("surname")+"\"," +
                "  \"client_email\":  \""+memberQuoteDetailsFull.get("client_email")+"\"," +
                "  \"client_phone_home\":  \""+memberQuoteDetailsFull.get("client_phone_home")+"\"," +
                "  \"client_phone_mobile\":  \""+memberQuoteDetailsFull.get("client_phone_mobile")+"\"," +
                "  \"address_1\":  \""+memberQuoteDetailsFull.get("client_address_1")+"\"," +
                "  \"town\":  \""+memberQuoteDetailsFull.get("client_town")+"\"," +
                "  \"county\":  \""+memberQuoteDetailsFull.get("client_county")+"\"," +
                "  \"country\": \""+memberQuoteDetailsFull.get("client_country")+"\"," +
                "  \"nationality\": \""+memberQuoteDetailsFull.get("client_nationality")+"\"," +
                "  \"client_postcode\":  \""+memberQuoteDetailsFull.get("client_postcode")+"\"," +
                "  \"client_resident\":  \""+memberQuoteDetailsFull.get("client_resident")+"\"," +
                "  \"gender\":  \""+memberQuoteDetailsFull.get("gender_")+"\"," +
                "  \"no_marketing\": \""+memberQuoteDetailsFull.get("no_marketing")+"\"," +
                "  \"see_report\": \""+memberQuoteDetailsFull.get("display_commission")+"\"," +
                "  \"jobs\": [" +
                "    { \"client_job_title\": \""+memberQuoteDetailsFull.get("id_client_job_title-container")+"\", \"client_employed\": \""+memberQuoteDetailsFull.get("client_employed")+"\", \"client_employment_status_other\": \"\" }" +
                "      ]," +
                "  \"client_debit_day\": \""+memberQuoteDetailsFull.get("client_debit_day").replace("st","").replace("th","")+"\"," +
                "  \"client_bankacc_name\": \""+memberQuoteDetailsFull.get("display_commission")+"\"," +
                "  \"client_bankacc_branch_name\": \"Test\"," +
                "  \"client_bankacc_number\": \""+memberQuoteDetailsFull.get("client_bankacc_number")+"\"," +
                "  \"client_bankacc_sort_1\": \""+memberQuoteDetailsFull.get("client_bankacc_sort_1")+"\"," +
                "  \"client_bankacc_sort_2\": \""+memberQuoteDetailsFull.get("client_bankacc_sort_2")+"\"," +
                "  \"client_bankacc_sort_3\": \""+memberQuoteDetailsFull.get("client_bankacc_sort_3")+"\"," +
                "  \"policy_start\": \""+memberQuoteDetailsFull.get("policy_start")+"\"," +
                "  \"start_date\": \""+memberQuoteDetailsFull.get("start_date")+"\"," +
                "  \"price_per_month\": \""+memberQuoteDetailsFull.get("price_per_month")+"\"," +
                "  \"quote_no\": \"Q"+selenium.randomNumberBetweenTwoPoints(1234,9999)+"\"" +
                "}";

        System.out.println(data);

        // create the policy
        Response responseJSON = restCall(data,propertyfile.getPropValues(propfile,"ouServiceUrl")+"/product/BritishFriendly.IP.Protect.ShortTerm/CreatePolicy", true);
        memberQuoteDetailsFull.put("policyNumber",(responseJSON.jsonPath().getString("policyNumber")));
        memberQuoteDetailsFull.put("policyId",(responseJSON.jsonPath().getString("policyId")));
        // return the policy number from the json response
        return memberQuoteDetailsFull;
    }

    // setup the payment history for an existing policy
    public boolean setupPaymentHistory(String policyId, String startDate, String endDate) throws Exception {
        selenium.wait(5);
        String paymentUrl = propertyfile.getPropValues(propfile,"ouServiceUrl")+"/policy/"+policyId+"/CreateSamplePayments";
        // login to the service

        String data = "{" +
                "  \"dateFrom\": \""+startDate+"\"," +
                "  \"dateTo\":  \""+endDate+"\"" +
                "}";
        System.out.println(data);
        Response responseJSON = restCall(data,paymentUrl, false);
        return Boolean.parseBoolean(responseJSON.jsonPath().getString("success"));
    }

    private String loginRequest() throws Exception {
        String loginUrl = propertyfile.getPropValues(propfile,"ouServiceUrl")+"/security/login";

        String requestData = "{" +
                "  \"username\": \""+propertyfile.getPropValues(propfile,"staffPortalLoginNormal")+"\"," +
                "  \"password\":  \""+propertyfile.getPropValues(propfile,"staffPortalPasswordNormal")+"\"" +
                "}";

        Headers headers  = new Headers(new Header("Content-Type", "application/json"));

        Response response = given()
                .headers(headers)
                .body(requestData)
                .when()
                .post(loginUrl);

        assert (response.getStatusCode()==200);
        return response.getBody().asString();
    }

    // for making a REST JSON request
    private Response restCall(String requestData, String requestUrl, boolean auth) throws Exception {

        System.out.println(requestUrl);
        Headers headers  = new Headers();

        if (auth == true) {
            if (bearer.equals("")) {
                bearer = loginRequest();
                System.out.println(bearer);
                headers  = new Headers(new Header("Content-Type", "application/json"),new Header("Authorization","Bearer " + bearer));
            }
        }
        else {
            headers  = new Headers(new Header("Content-Type", "application/json"));
        }

        Response response = given()
                .headers(headers)
                .body(requestData)
                .when()
                .post(requestUrl);

        try {
            assert (response.statusCode() == (200)) : "Response is " + response.getStatusCode() + response.getStatusLine();
        }
        catch (AssertionFailed e) {
            System.out.println("Request failed");
        }
        return response;
    }

    // for getting a random name
    public Map<String,String> nameGenerator(String gender, String region) throws Exception {

        Response response = given()
                .when()
                .get("https://uinames.com/api/?region="+region+"&gender="+gender.toLowerCase());

        Map<String,String> nameDetails = new HashMap<>();
        nameDetails.put("name",response.jsonPath().getString("name"));
        nameDetails.put("surname",response.jsonPath().getString("surname"));
        return nameDetails;
    }
}