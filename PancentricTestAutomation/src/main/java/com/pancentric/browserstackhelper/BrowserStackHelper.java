package com.pancentric.browserstackhelper;

import java.net.InetAddress;
import java.net.URISyntaxException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import java.net.URL;

import java.util.List;

public class BrowserStackHelper {

  //private IMBrowserStackHelper instance;

  public BrowserStackHelper() throws URISyntaxException {
    super();     
  }

  public WebDriver getRemoteBrowserSingle(List<String> capabilities, String scenario, String username, String key, String project, String buildNo) throws Exception {
      
      // String user details
      String USERNAME = username;
      String AUTOMATE_KEY = key;
      String URL = "http://" + USERNAME + ":" + AUTOMATE_KEY + "@hub.browserstack.com/wd/hub";
      
      //Input capabilities
      DesiredCapabilities caps = new DesiredCapabilities();
      
      // set defaults which  can be overwritten
      //caps.setCapability("browser", "Chrome");
      //caps.setCapability("browser_version", "30.0");
      //caps.setCapability("os", "Windows");
      //caps.setCapability("os_version", "10");
      
      String hostname = InetAddress.getLocalHost().getHostName();
      System.out.println(hostname);
      
      // always true
      caps.setCapability("browserstack.debug", "true");
      caps.setCapability("project",project);
      caps.setCapability("build",buildNo);
      caps.setCapability("name", scenario);
      //caps.setCapability("browserstack.local", "true");
      //caps.setCapability("browserstack.localIdentifier", "info");
      caps.setCapability("browserstack.selenium_version", "2.52.0");
      caps.setCapability("resolution","1366x768");      
      
      //resolution
      // Set the resolution of VM before beginning of your test.
      // Windows (XP,7): 800x600, 1024x768, 1280x800, 1280x1024, 1366x768, 1440x900, 1680x1050, 1600x1200, 1920x1200, 1920x1080, 2048x1536

      // Windows (8,8.1,10): 1024x768, 1280x800, 1280x1024, 1366x768, 1440x900, 1680x1050, 1600x1200, 1920x1200, 1920x1080, 2048x1536

      // OS X: 1024x768, 1280x960, 1280x1024, 1600x1200, 1920x1080

      // Default: 1024x768
      
      //deviceOrientation
      
      // set passed capabilities
      int i = 0;
      while (i < capabilities.size()) {
          String[] capability = capabilities.get(i).split(":");
          caps.setCapability(capability[0], capability[1]);
          i++;
      }
      
      WebDriver driver = new RemoteWebDriver(new URL(URL), caps);
      System.out.println(caps.toString());
      return driver;
  }
}
