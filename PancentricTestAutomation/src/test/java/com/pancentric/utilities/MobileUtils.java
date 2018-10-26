package com.pancentric.utilities;

import io.appium.java_client.MultiTouchAction;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import io.appium.java_client.touch.offset.ElementOption;
import io.appium.java_client.touch.offset.PointOption;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.support.PageFactory;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static io.appium.java_client.touch.LongPressOptions.longPressOptions;
import static io.appium.java_client.touch.TapOptions.tapOptions;
import static io.appium.java_client.touch.offset.ElementOption.element;
import static io.appium.java_client.touch.offset.PointOption.point;


public abstract class MobileUtils extends AppiumBase{

     //   protected AndroidDriver driver;

        public MobileUtils(AndroidDriver driver){
            this.driver = driver;
            //this Duration part is because i upgrade to Appium Java client 6.0.0
            Duration duration = Duration.of(10, ChronoUnit.SECONDS);
            PageFactory.initElements(new AppiumFieldDecorator(driver, duration), this);
        }

        //Tell developer to enable WebView debugguing, call the static setWebContentsDebuggingEnabled on the WebView class.
        public void findContext(){
            Set<String> s = driver.getContextHandles();
            for(String handle : s)
            {
                System.out.println(handle);
            }
            //switch to the context handle keyword like: driver.context("NATIVE_APP or WEBVIEW_com.example.testapp");
        }

        public void tap(WebElement element){
            TouchAction touch = new TouchAction(driver);
            touch.tap(tapOptions().withElement(element(element))).perform();
        }

        public void press(WebElement element ){
            TouchAction touch = new TouchAction(driver);
            touch.press(element(element)).release().perform();
        }

        public void swipe(WebElement startEl, WebElement SwipeTillEl){
            TouchAction touch = new TouchAction(driver);
            touch.longPress(longPressOptions().withElement(element(startEl)).withDuration(Duration.ofMillis(2000))).moveTo(element(SwipeTillEl)).release().perform();
        } //reduce the awaitAction for faster swipe

        public void tapWithCoodinates(int x, int y ){
            TouchAction touch = new TouchAction(driver);
            touch.tap(point(x, y)).perform();
        }

        //android method
        public WebElement getElementByBounds(int a, int b, int c, int d){
            WebElement element = driver.findElementByXPath("//android.view.View[@bounds='[" + a +"," + b + "][" + c + "," + d + "]']");
            return element;
        }

        public void swipe(int startx, int starty, int endx, int endy ){
            TouchAction touch = new TouchAction(driver);
            //find co-ordinates in top right corner of ui Automator tool
            touch.longPress(longPressOptions().withPosition(point(startx, starty)).withDuration(Duration.ofMillis(2000))).moveTo(point(endx, endx)).release().perform();
        } //if the co-ordinates are on a clickable element then this will not work

        public void swipe( WebElement startElement, int endx, int endy){
            TouchAction touch = new TouchAction(driver);
            touch.longPress(longPressOptions().withElement(element(startElement)).withDuration(Duration.ofMillis(2000))).moveTo(point(endx, endy)).release().perform();
        } //find co-ordinates in top right corner of ui Automator

        //android method
        public void scrollTextInToView(String elementText){
            driver.findElementByAndroidUIAutomator("new UiScrollable(new UiSelector()).scrollIntoView(text(\"" + elementText + "\"));");
            // (text(\"WebView\"));"); escape the double quotes

        }

        public void multiTouch(int x1, int y1, int x2, int y2){
            TouchAction touch1 = new TouchAction(driver).longPress(point(x1, y1)).release().perform();
            TouchAction touch2 = new TouchAction(driver).longPress(point(x2, y2)).release().perform();
            MultiTouchAction mTouch = new MultiTouchAction(driver);
            mTouch.add(touch1).add(touch2).perform();
        } //more touch action can be added

        public void dragAndDrop(WebElement element, WebElement elementToMoveTo){
            TouchAction touch = new TouchAction(driver);
            touch.longPress(longPressOptions().withElement(element(element))).moveTo(element(elementToMoveTo)).release().perform();
        }

        public static String takeScreenShot(String filename) {
            filename = filename.replaceAll("[^a-zA-Z0-9/]", "");
            File file = new File(filename + ".png");
            File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            try {
                FileUtils.copyFile(scrFile, new File("target/screenshots/" + filename + ".png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "target/screenshots/" + filename + ".png";
        }

        public void longPress(int x, int y){
            TouchAction touch = new TouchAction(driver);
            touch.longPress(point(x,y)).release().perform();
        }

        public void longPress(WebElement element){
            TouchAction touch = new TouchAction(driver);
            touch.longPress(longPressOptions().withElement(element(element))).release().perform();
        }

        public static String getTimeStamp() {
            Date date = new Date();
            String todaysdate = new Timestamp(date.getTime()).toString();
            todaysdate = todaysdate.replace(".", "");
            todaysdate = todaysdate.replace(":", "");
            todaysdate = todaysdate.replace(" ", "-");
            return todaysdate;
        }

        public void switchContextView(String context){
            //NATIVE_CONTEXT-browser, NATIVE_APP- app, WEBVIEW-hybrid app
            (driver).context(context);

        }


        public void scrollHelper(){
            Dimension size = driver.manage().window().getSize();
            int x = size.getWidth() /2;
            int starty=(int) (size.getHeight() * 0.60);
            int endy = (int) (size.getHeight() * 0.10);
            swipe(x, starty, x, endy);
        }

        public void scrollWithJavascript(WebElement element){
            JavascriptExecutor jse = ((JavascriptExecutor) driver);
            jse.executeScript("arguments[0].scrollIntoView(true);", element);
        } //for mobile web you can only you this method to scroll


    }


