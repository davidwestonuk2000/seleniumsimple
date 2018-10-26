package com.pancentric.bromham.runners;

//import com.infomentum.imbrowserstackhelper.Parallelized;

import com.pancentric.utilities.Parallelized;
import com.pancentric.utilities.SeleniumWebdriver;
import com.pancentric.utilities.StopWatchTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.LinkedList;

@RunWith(Parallelized.class)
public class ParallelTestKeepAlive {
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

//add more browsers here

        return env;
    }

    /**
     * @param platform
     * @param browserName
     * @param browserVersion
     */
    public ParallelTestKeepAlive(String platform, String browserName, String browserVersion) {
        this.platform = platform;
        this.browserName = browserName;
        this.browserVersion = browserVersion;
    }

    private WebDriver driver;

    @Before
    public void setUp() throws Exception {
        DesiredCapabilities capability = new DesiredCapabilities().chrome();

        ChromeOptions chromeOptions = new ChromeOptions();
        //chromeOptions.addArguments("--headless");
        capability.setCapability(ChromeOptions.CAPABILITY, chromeOptions);

        //capability.setCapability("build", "JUnit-Parallel");
        capability.setCapability("name", "Parallel test");
        // driver = new RemoteWebDriver(new URL("http://10.22.70.133:4444/wd/hub"), capability);
    }

    @Test
    public void testSimple() throws Exception {
        SeleniumWebdriver selenium = new SeleniumWebdriver("Chrome", "https://openunderwriter.dev.b13y.com/", "");
        selenium.loadPageSameWindow("https://openunderwriter.dev.b13y.com/");
        selenium.wait(10);
        selenium.type(SeleniumWebdriver.Locators.partialid,"58_login","adam@openunderwriter.org");
        selenium.type(SeleniumWebdriver.Locators.partialid,"password","adam");
        selenium.click(SeleniumWebdriver.Locators.partialclass,"btn-primary");

        selenium.wait(10);

        selenium.closeTheBrowser(false);
    }

    @After
    public void tearDown() throws Exception {

    }
}