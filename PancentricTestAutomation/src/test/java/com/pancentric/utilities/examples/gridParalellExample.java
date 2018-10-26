package com.pancentric.utilities.examples;

//import com.infomentum.imbrowserstackhelper.Parallelized;
import static org.junit.Assert.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
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

@RunWith(Parallelized.class)
public class gridParalellExample {
  private String platform;
  private String browserName;
  private String browserVersion;

  @Parameterized.Parameters
  public static LinkedList getEnvironments() throws Exception {
    LinkedList<String[]> env = new LinkedList();

    env.add(new String[]{Platform.WINDOWS.toString(),"chrome", "65"});
    env.add(new String[]{Platform.WINDOWS.toString(),"chrome", "65"});
    env.add(new String[]{Platform.WINDOWS.toString(),"chrome", "65"});
    env.add(new String[]{Platform.WINDOWS.toString(),"chrome", "65"});
    env.add(new String[]{Platform.WINDOWS.toString(),"chrome", "65"});
    //env.add(new String[]{Platform.WINDOWS.toString(),"firefox", "31"});

//add more browsers here

    return env;
  }

    /**
     * @param platform
     * @param browserName
     * @param browserVersion
     */
    public gridParalellExample(String platform, String browserName, String browserVersion) {
    this.platform = platform;
    this.browserName = browserName;
    this.browserVersion = browserVersion;
  }

  private WebDriver driver;

  @Before
  public void setUp() throws Exception {
    DesiredCapabilities capability = new DesiredCapabilities().chrome();

    //capability.setCapability("platform", platform);
    //capability.setCapability("browser", browserName);
    //capability.setVersion("31");
    
    ChromeOptions chromeOptions = new ChromeOptions();
    chromeOptions.addArguments("--headless");
    capability.setCapability(ChromeOptions.CAPABILITY, chromeOptions);

    //capability.setCapability("build", "JUnit-Parallel");
    capability.setCapability("name", "Parallel test");
    driver = new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"), capability);  
  }

  @Test
  public void testSimple() throws Exception {
    driver.get("http://www.motability.co.uk");
    String title = driver.getTitle();
    System.out.println("Page title is: " + title);

  }

  @After
  public void tearDown() throws Exception {
    driver.quit();
  }
}
