package com.pancentric.utilities;


import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.remote.AutomationName;
import io.appium.java_client.remote.MobileCapabilityType;
import io.appium.java_client.remote.MobilePlatform;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

    public class IOSAppiumBase {

        protected static AppiumDriver driver = null;
        public static WebDriverWait wait;
        private static String deviceType;
        private static String contextType;


        public static void createIOSdriver(String device, String context) throws MalformedURLException, InterruptedException {
            deviceType = device;
            DesiredCapabilities cap = new DesiredCapabilities();
            cap.setCapability(MobileCapabilityType.PLATFORM_NAME, MobilePlatform.IOS);
            if (device.equalsIgnoreCase("simulator")) {
                //this shone be the name of the iphone simulator
                cap.setCapability(MobileCapabilityType.DEVICE_NAME, "iPhone 7"); //iPhone Simulator
                cap.setCapability(MobileCapabilityType.PLATFORM_VERSION, "10.2");
            }else if (device.equalsIgnoreCase("real")){
             //   cap.setCapability(MobileCapabilityType.DEVICE_NAME, "Iphone");
            } else {
                System.out.println("Error: check properties file if 'DEVICE' is set correctly");
                //stop execution of program here
                System.exit(1);
            }
            contextType = context;
            if (context.equalsIgnoreCase("browser")) {
                //working with the mobile browser safari
                cap.setCapability(MobileCapabilityType.BROWSER_NAME, "safari");
                cap.setCapability(MobileCapabilityType.AUTOMATION_NAME, AutomationName.IOS_XCUI_TEST);
                cap.setCapability(MobileCapabilityType.APPIUM_VERSION, "1.8.1");
            }
            else if (context.equalsIgnoreCase("APP")) {

                //     File appDir = new File("src/test/resources");
                //      File app = new File(appDir, "UICatalog.app"); //tippo3000.app
                //      cap.setCapability(MobileCapabilityType.APP, app.getAbsolutePath());
                cap.setCapability("app", "settings");
                cap.setCapability("automationName", "XCUITest");
                //in order to use Tap() you need to set nativeWebTap = true
                cap.setCapability("nativeWebTap", true);
                //set to true so app does not reinstall again
                cap.setCapability("no-Reset", true);
                //removes cache data this will reset the app = false
                cap.setCapability("full-Reset", false);
                cap.setCapability("--session-override", true);
                //  cap.setCapability("waitForQuiescence", false);
                //show the xcode log, may help with debuging
                //cap.setCapability("showXcodeLog", true);
            } else {
                System.out.println("Error: check properties file if 'CONTEXT' is set correctly");
                //stop execution of program here
                System.exit(1);
            }
            driver = new IOSDriver(new URL("http://127.0.0.1:4723/wd/hub"), cap);
            //How long (in seconds) Appium will wait for a new command from the client before quit and ending the session
            cap.setCapability(MobileCapabilityType.NEW_COMMAND_TIMEOUT, 100);
            wait = new WebDriverWait(driver, 10);
        }



        public static AppiumDriver getAppiumDriver(String device, String context) throws MalformedURLException, InterruptedException {
            if(driver == null){
                createIOSdriver(deviceType, contextType);
            }
            return driver;
        }

/*
For some reason your appium session is finished before quit is called.
It can be related to appium service exception, or session timeout - check service logs and you will understand it.
It makes sense to check session status before calling quit to avoid errors.
 */
        public static void closerDriver() {
            try {
                driver.closeApp();
                driver.quit();
            } catch (Exception e) {
                System.out.println("Warning: Appium session may have finished before quit() is called");
                System.out.println(e.getMessage());
            }
            driver = null;
        }


}
