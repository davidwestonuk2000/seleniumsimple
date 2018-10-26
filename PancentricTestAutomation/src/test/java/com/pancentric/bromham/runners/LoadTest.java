package com.pancentric.bromham.runners;


//import com.infomentum.imbrowserstackhelper.Parallelized;
import static org.junit.Assert.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

import com.pancentric.bromham.BromhamStepDefinitions;
import com.pancentric.bromham.actions.JPBMActions;
import com.pancentric.bromham.actions.QuoteAndApplyActions;
import com.pancentric.bromham.actions.StaffPortalActions;
import com.pancentric.utilities.*;
import cucumber.api.Scenario;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Platform;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.chrome.ChromeOptions;

@RunWith(ConcurrentJunitRunner.class)
@Concurrent(threads = 1)


public final class LoadTest {

    private enum testScenarios {
        claim,
        premiumHoliday,
        increaseBenefit,
        decreaseBenefit,
        changeOccupation,
        changeBankDetails,
        addNote,
        uploadDocument,
        cancelWithRefund,
        cancelAndReinstate,
        cancelNoRefund,
        changeAddress;
    }

//    FileDownloader download = new FileDownloader();
//    PropertyFetcher propertyfile = new PropertyFetcher();
//    // locate the appropriate prop file
//    String propfile = BromhamStepDefinitions.projectName + "/properties/"+ BromhamStepDefinitions.environment;
//    String urlHREF = propertyfile.getPropValues(propfile,"dataFileLocation");
//    download.fileDownloaderNonBrowser(urlHREF, "target/", "data.xlsx", 10);

    @Test public void test0() throws Throwable { printAndWait(); }
    //@Test public void test1() throws Throwable { printAndWait(); }
    //@Test public void test2() throws Throwable { printAndWait(); }
    //@Test public void test3() throws Throwable { printAndWait(); }
    //@Test public void test4() throws Throwable { printAndWait(); }



    void printAndWait() throws Throwable {

        //System.setProperty("project", "bromham");

        SeleniumWebdriver selenium = new SeleniumWebdriver(System.getProperty("browser"), "http://www.google.co.uk", "test");
        QuoteAndApplyActions quote = new QuoteAndApplyActions(selenium);
        StaffPortalActions staff = new StaffPortalActions(selenium);
        Map<String, String> memberDetails = quote.createPolicyRest("valid-simplified");
        String policyNumber = memberDetails.get("policyNumber");
        System.out.println(policyNumber);
        System.out.println(memberDetails.get("policyId"));
        int duration = 1;


        // check for the workflow in jpbm - pause for policy creation
        JPBMActions jpbm = new JPBMActions();
        String memberName = memberDetails.get("title") + ". " + memberDetails.get("name") + " " + memberDetails.get("surname");
        jpbm.verifyStartWorkflowForPolicy(memberDetails.get("policyId"), memberName, 30, 5000);

        boolean gotPolicy = true;
        staff.openStaffPortal();
        staff.login("Normal");


        long end = System.currentTimeMillis() + ((duration * 60) * 1000);

        // hashmap to store the filetype ratios
        HashMap<testScenarios, Integer> hmap = new HashMap();
        hmap.put(testScenarios.claim, 1);
        hmap.put(testScenarios.changeAddress, 60); // GOOD
        hmap.put(testScenarios.increaseBenefit, 80);
        hmap.put(testScenarios.premiumHoliday, 20); // GOOD
        hmap.put(testScenarios.addNote, 100); // GOOD
        hmap.put(testScenarios.changeOccupation, 50); // GOOD
        hmap.put(testScenarios.changeBankDetails, 90); // GOOD
        hmap.put(testScenarios.uploadDocument, 70); // GOOD
        hmap.put(testScenarios.cancelAndReinstate, 12);
        hmap.put(testScenarios.cancelNoRefund, 10);
        hmap.put(testScenarios.cancelWithRefund, 5);

        // Transfer files via SFTP

        // loop for the required duration
        while (System.currentTimeMillis() < end) {

            if (gotPolicy == false) {
                memberDetails = quote.createPolicyRest("valid-simplified");
                policyNumber = memberDetails.get("policyNumber");
                System.out.println(policyNumber);
                System.out.println(memberDetails.get("policyId"));
                gotPolicy = true;
            }

            int counter = 0;
            System.out.println("durationlooper");
            // loop for the required batch size
            System.out.println("counter" + counter);
            // set the default filetype
            testScenarios selectedScenario = testScenarios.claim;

            // random number to drive the file type
            int randomNumber = 12;//selenium.randomNumberBetweenTwoPoints(1, 100);

            // sort the filetype hashmap ascending
            Map<testScenarios, Integer> map = sortByValues(hmap);
            Set set2 = map.entrySet();
            Iterator iterator2 = set2.iterator();

            // loop the file types to locate the one matching the random number
            file_type_finder:
            while (iterator2.hasNext()) {
                Map.Entry me2 = (Map.Entry) iterator2.next();

                // map the random number to a file type
                if (randomNumber <= Integer.parseInt(me2.getValue().toString())) {
                    selectedScenario = testScenarios.valueOf(me2.getKey().toString());
                    break file_type_finder;
                }
            }
            System.out.println(selectedScenario);
            System.out.println(randomNumber);
            // write an xml file of the required filetype
            staff.openStaffPortal();
            staff.searchByPolicyId(policyNumber);

            try {

                if (selectedScenario == testScenarios.claim) {
                    staff.createANewClaim();
                    staff.verifyClaimPaymentsSetup();
                } else if (selectedScenario == testScenarios.changeAddress) {
                    staff.changeAddress();
                    staff.verifyChangeOfAddress();
                } else if (selectedScenario == testScenarios.changeBankDetails) {
                    staff.addNewBankDetails();
                    staff.verifyBankDetailsAdd();
                } else if (selectedScenario == testScenarios.changeOccupation) {
                    staff.changeOccupation();
                    staff.verifyChangeOfOccupation();
                } else if (selectedScenario == testScenarios.addNote) {
                    staff.addNotePolicy();
                    staff.verifyPolicyNote();
                } else if (selectedScenario == testScenarios.uploadDocument) {
                    staff.uploadADocumentToPolicy();
                    staff.verifyPolicyDocumentUpload();
                } else if (selectedScenario == testScenarios.premiumHoliday) {
                    staff.addPremiumHoliday();
                    staff.verifyPremiumHoliday();
                    gotPolicy = false;
                } else if (selectedScenario == testScenarios.increaseBenefit) {
                    staff.addAPremiumTranch();
                } else if (selectedScenario == testScenarios.cancelNoRefund) {
                    staff.cancelPolicy("Cooling off", false);
                    staff.verfiyPolicyStatus("Cancellation Pending");
                    gotPolicy = false;
                } else if (selectedScenario == testScenarios.cancelWithRefund) {
                    staff.cancelPolicy("Other", true);
                    staff.verifyCancellationRefund();
                    gotPolicy = false;
                } else if (selectedScenario == testScenarios.cancelAndReinstate) {
                    staff.cancelPolicy("Other", false);
                    staff.reinstateAPolicy("");
                    staff.verfiyPolicyStatus("Active");
                } else {

                }
            }
            catch (Exception e) {

                System.out.println(e.getMessage());
                System.out.println("IN EXCEPTION");
                System.out.println(e.getStackTrace().toString());
                //staff.openStaffPortal();

                throw(e);
            }


        }
        selenium.closeTheBrowser(false);
    }
    private static HashMap sortByValues(HashMap map) {
        List list = new LinkedList(map.entrySet());
        // Defined Custom Comparator here
        Collections.sort(list, new Comparator() {
            public int compare(Object o1, Object o2) {
                return ((Comparable) ((Map.Entry) (o1)).getValue())
                        .compareTo(((Map.Entry) (o2)).getValue());
            }
        });

        // Here I am copying the sorted list in HashMap
        // using LinkedHashMap to preserve the insertion order
        HashMap sortedHashMap = new LinkedHashMap();
        for (Iterator it = list.iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            sortedHashMap.put(entry.getKey(), entry.getValue());
        }
        return sortedHashMap;
    }

}
