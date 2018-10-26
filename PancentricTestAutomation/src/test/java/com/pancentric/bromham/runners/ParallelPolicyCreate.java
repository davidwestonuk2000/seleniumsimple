package com.pancentric.bromham.runners;


//import com.infomentum.imbrowserstackhelper.Parallelized;
import static org.junit.Assert.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;

import com.pancentric.bromham.actions.JPBMActions;
import com.pancentric.bromham.actions.QuoteAndApplyActions;
import com.pancentric.bromham.actions.StaffPortalActions;
import com.pancentric.utilities.SeleniumWebdriver;
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
import com.pancentric.utilities.Parallelized;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import com.pancentric.utilities.ConcurrentJunitRunner;
import com.pancentric.utilities.Concurrent;

@RunWith(ConcurrentJunitRunner.class)
@Concurrent(threads = 5)
public final class ParallelPolicyCreate {

    @Test public void test0() throws Throwable { printAndWait(); }
    @Test public void test1() throws Throwable { printAndWait(); }
    @Test public void test2() throws Throwable { printAndWait(); }
    @Test public void test3() throws Throwable { printAndWait(); }
    @Test public void test4() throws Throwable { printAndWait(); }


    void printAndWait() throws Throwable {
        SeleniumWebdriver selenium = new SeleniumWebdriver(System.getProperty("browser"), "http://www.google.co.uk","test");
        QuoteAndApplyActions quote = new QuoteAndApplyActions(selenium);
        StaffPortalActions staff = new StaffPortalActions(selenium);

        int i = 0;
        while (i < 50) {

            Map<String,String> memberDetails = quote.createPolicyRest("valid-simplified");
            String policyNumber = memberDetails.get("policyNumber");
            System.out.println(policyNumber);
            System.out.println(memberDetails.get("policyId"));

            // check for the workflow in jpbm - pause for policy creation
            JPBMActions jpbm = new JPBMActions();
            String memberName = memberDetails.get("title") + ". " + memberDetails.get("name") + " " + memberDetails.get("surname");
            jpbm.verifyStartWorkflowForPolicy(memberDetails.get("policyId"), memberName,30,5000);

            // open staff portal and login and search for the created policy
            staff.openStaffPortal();
            if (i < 1) {
                staff.login("Normal");
            }
            staff.searchByPolicyId(policyNumber);

            i++;
            System.out.println(Thread.currentThread().getId() + " - " + i);

        }
        selenium.closeTheBrowser(false);
    }

}
