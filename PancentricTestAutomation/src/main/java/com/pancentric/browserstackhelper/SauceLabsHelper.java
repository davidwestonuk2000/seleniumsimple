package com.pancentric.browserstackhelper;

import java.net.InetAddress;
import java.net.URISyntaxException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import java.net.URL;

import java.util.List;

public class SauceLabsHelper {

  private SauceLabsHelper instance;

  public SauceLabsHelper() throws URISyntaxException {
    super();     
  }

  public WebDriver getRemoteBrowserSingle(List<String> capabilities, String scenario, String username, String key, String project, String buildNo) throws Exception {
      
      // String user details
      String USERNAME = username;
      String AUTOMATE_KEY = key;
      final String URL = "http://" + USERNAME + ":" + AUTOMATE_KEY + "@ondemand.saucelabs.com:80/wd/hub";
    
      //String URL = "https://" + USERNAME + ":" + AUTOMATE_KEY + "@ondemand.saucelabs.com:443/wd/hub";
      System.out.println(URL);
      
      System.out.println(USERNAME);
      System.out.println(AUTOMATE_KEY);
      
      
      DesiredCapabilities caps = DesiredCapabilities.chrome();
      caps.setCapability("platform", "Windows XP");
      caps.setCapability("version", "43.0");
      // set defaults which  can be overwritten
      //DesiredCapabilities caps = new DesiredCapabilities();
      //caps.setCapability("browserName", "Chrome");
      //caps.setCapability("platform", "Windows XP");
      //caps.setCapability("version", "43.0");
      //caps.setCapability("screenResolution", "1366x768");
      //caps.setCapability("build",buildNo);
      //caps.setCapability("name", scenario);
      //caps.setCapability("seleniumVersion", "2.52.0");
      
      String hostname = InetAddress.getLocalHost().getHostName();
      System.out.println(hostname);
      

      
      //resolution
      // Set the resolution of VM before beginning of your test.
      // Windows (XP,7): 800x600, 1024x768, 1280x800, 1280x1024, 1366x768, 1440x900, 1680x1050, 1600x1200, 1920x1200, 1920x1080, 2048x1536

      // Windows (8,8.1,10): 1024x768, 1280x800, 1280x1024, 1366x768, 1440x900, 1680x1050, 1600x1200, 1920x1200, 1920x1080, 2048x1536

      // OS X: 1024x768, 1280x960, 1280x1024, 1600x1200, 1920x1080

      // Default: 1024x768
      
      WebDriver driver = new RemoteWebDriver(new URL(URL), caps);
      System.out.println(caps.toString());
      return driver;
  }
}
