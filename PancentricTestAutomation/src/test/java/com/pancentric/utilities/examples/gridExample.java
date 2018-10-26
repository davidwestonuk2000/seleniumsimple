package com.pancentric.utilities.examples;

import java.net.URL;

import org.openqa.selenium.By;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

public class gridExample {
    public static void main(String[] args) throws Exception {

        //Input capabilities
        DesiredCapabilities capability = DesiredCapabilities.firefox();
        capability.setVersion("31");
        //capability.(Platform.WINDOWS.toString());
        WebDriver driver =
            new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"),
                                capability);

        driver.get("http://www.google.com/");
        WebElement element = driver.findElement(By.name("q"));

        element.sendKeys("BrowserStack");
        element.submit();

        System.out.println("And the Title is: " + driver.getTitle());
        driver.quit();

    }
}
