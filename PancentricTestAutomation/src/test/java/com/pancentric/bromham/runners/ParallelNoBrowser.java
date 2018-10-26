package com.pancentric.bromham.runners;


//import com.infomentum.imbrowserstackhelper.Parallelized;
import static org.junit.Assert.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.Random;

import com.pancentric.bromham.actions.QuoteAndApplyActions;
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
@Concurrent(threads = 2)
public final class ParallelNoBrowser {

    @Test public void test0() throws Throwable { printAndWait(); }
    @Test public void test1() throws Throwable { printAndWait(); }
    @Test public void test2() throws Throwable { printAndWait(); }


    void printAndWait() throws Throwable {
        SeleniumWebdriver selenium = null;//new SeleniumWebdriver(System.getProperty("browser"), "http://www.google.co.uk","test");
        QuoteAndApplyActions quote = new QuoteAndApplyActions(selenium);

        int i = 0;
        while (i < 20) {
            quote.createPolicyRest("valid-simplified");
            i++;
            System.out.println(Thread.currentThread().getId() + " - " + i);

        }

    }
}
