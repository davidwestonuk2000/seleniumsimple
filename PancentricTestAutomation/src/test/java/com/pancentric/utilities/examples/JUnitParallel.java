package com.pancentric.utilities.examples;

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

@RunWith(Parallelized.class)
public class JUnitParallel {
	private String platform;
	private String browserName;
	private String browserVersion;

	@Parameterized.Parameters
	public static LinkedList<String[]> getEnvironments() throws Exception {
		LinkedList<String[]> env = new LinkedList<String[]>();
		env.add(new String[]{Platform.WINDOWS.toString(), "chrome", "50"});
		env.add(new String[]{Platform.WINDOWS.toString(),"firefox","latest"});
		env.add(new String[]{Platform.WINDOWS.toString(),"ie","9"});

		//add more browsers here

		return env;
	}


	public JUnitParallel(String platform, String browserName, String browserVersion) {
		this.platform = platform;
		this.browserName = browserName;
		this.browserVersion = browserVersion;
	}

	private WebDriver driver;

	@Before
	public void setUp() throws Exception {
		DesiredCapabilities capability = new DesiredCapabilities();
		capability.setCapability("platform", platform);
		capability.setCapability("browser", browserName);
		capability.setCapability("browserVersion", browserVersion);
		capability.setCapability("name", "Parallel test");
		driver = new RemoteWebDriver(
			new URL("https://key:secret@hub.testingbot.com/wd/hub"),
			capability
		);
	}

	@Test
	public void testSimple() throws Exception {
		driver.get("http://www.google.com");
		String title = driver.getTitle();
		System.out.println("Page title is: " + title);
		WebElement element = driver.findElement(By.name("q"));
		element.sendKeys("TestingBot");
		element.submit();
		driver = new Augmenter().augment(driver);
		File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		try {
			FileUtils.copyFile(srcFile, new File("Screenshot.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@After
	public void tearDown() throws Exception {
		driver.quit();
	}
}