package com.pancentric.utilities;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.*;
import static org.junit.Assert.*;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.Platform;
import org.openqa.selenium.TakesScreenshot;

public class PhantomJS {

    public static void main(String[] argv) {
        // prepare capabilities
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability("takesScreenshot", true); 
        caps.setCapability(
            PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY,
            "C:\\dev\\seleniumTests\\seleniumTests\\Drivers\\phantomjs.exe"
        );

        WebDriver driver = new PhantomJSDriver(caps);

        // Load Google.com
        driver.get("http://www.google.com");
        // Locate the Search field on the Google page
        WebElement element = driver.findElement(By.name("q"));
        // Type Cheese
        String strToSearchFor = "Cheese!";
        element.sendKeys(strToSearchFor);
        // Submit form
        element.submit();

        // Check results contains the term we searched for
        assertTrue(driver.getTitle().toLowerCase().contains(strToSearchFor.toLowerCase()));
        System.out.println(driver.getTitle());

        // done
        driver.quit();
    }
}