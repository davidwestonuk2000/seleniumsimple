package com.pancentric.utilities;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.remote.MobileCapabilityType;
import io.appium.java_client.remote.MobilePlatform;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.WebDriverWait;


import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.Date;

public class AppiumBase {

    protected static AndroidDriver driver = null;
    public static WebDriverWait wait;
    private static String deviceType;
    private static String contextType;
    public static String url;


    // public static void createNewMobBrowserDriver(String device, String context) throws MalformedURLException, InterruptedException  {
    public static void createNewMobileDriver(String device, String context) throws MalformedURLException, InterruptedException  {
        deviceType = device;
        contextType = context;
        // relative path to .apk file, use System.getproperty("project") to make this flexible across different projects
        File appDir = new File("src/test/resources/com/infomentum/" + System.getProperty("project") + "/Apps");
        File app = new File(appDir, "ApiDemos-debug.apk");

        //set Capabilities for the type of device (Emulator or Real device)
        DesiredCapabilities cap = new DesiredCapabilities();

        if(device.equalsIgnoreCase("emulator")){
            cap.setCapability(MobileCapabilityType.PLATFORM_NAME, MobilePlatform.ANDROID);
            cap.setCapability(MobileCapabilityType.DEVICE_NAME, "Android Emulator");
            cap.setCapability("avd", "PixelApi25");
        } else if(device.equalsIgnoreCase("real")) {
            cap.setCapability(MobileCapabilityType.DEVICE_NAME, "Android Device");
        } else {
            System.out.println("check property file if 'DEVICE' has been set");
            //stop execution of program here
            System.exit(1);
        }
        //set Capabilities for the type Application (Native App or browser App)
        if (context.equalsIgnoreCase("native-app")) {
            //you may need to set the caps for appActivity & appPackage
            // get full path for APP to load on device
            cap.setCapability(MobileCapabilityType.APP, app.getAbsolutePath());
            //set this so that app does not reinstall again
            cap.setCapability("no-Reset", false);
            //removes cache data this will reset the app = false
            cap.setCapability("full-Reset", false);
            cap.setCapability("--session-override", true);
        } else if (context.equalsIgnoreCase("mobile-browser")) {
            //you may need to set the caps for appActivity & appPackage for the browser
            cap.setCapability(MobileCapabilityType.BROWSER_NAME, "Chrome");
            //cap.setCapability("appPackage", "com.android.chrome");
            //cap.setCapability("appActivity","Main");
        } else {
            System.out.println("check property file if 'CONTEXT' has been set");
            //stop execution of program here
            System.exit(1);
        }

        // initializing driver object
        driver = new AndroidDriver(new URL("http://127.0.0.1:4723/wd/hub"), cap);
        cap.setCapability(MobileCapabilityType.NEW_COMMAND_TIMEOUT, 100);
        wait = new WebDriverWait(driver, 10);
        if (context.equalsIgnoreCase("mobile-browser")) {
            driver.get(new PropertyReader().readProperty("url"));
        }
    }

    public static AndroidDriver getAppiumDriver() throws MalformedURLException, InterruptedException {
        if(driver == null){
            createNewMobileDriver(deviceType, contextType);
        }
        return driver;
    }

    //find a way to shut down driver properly
    public static void closerDriver() {
      try{
          // driver.closeApp(); //or close();
          // driver.quit();
            driver.closeApp();
            driver = null;
        } catch (Exception e){
          System.out.println("Error: error with terminating driver");
      }
    }

   }

// you can find out if driver is still active by using driver toSting and see if you get a long number or if its null
// System.out.println("is driver died1111111111111111" + driver.toString());