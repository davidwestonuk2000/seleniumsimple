package com.pancentric.utilities;

import java.io.File;


import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import io.appium.java_client.service.local.flags.GeneralServerFlag;
import java.net.ServerSocket;
import java.io.IOException;




public class AppiumServer {

        public static AppiumDriverLocalService service;
        private static String Appium_Node_Path = "";
        private static String Appium_JS_Path = "";


        public static void startAppiumServer() throws Exception {
            //first check what operating system (android or IOS) to determine which driver to create
            String osName = System.getProperty("os.name").toLowerCase();
//            Windows path
            if(osName.contains("windows")){
                Appium_Node_Path = "C:/node.exe";
                Appium_JS_Path = "C:/node_modules/appium/build/lib/main.js"; //use main.js instead of appium.js
            }
            if (osName.contains("mac")) {
//            mac path
                Appium_Node_Path = "/usr/local/bin/node";
                Appium_JS_Path = "/Users/infomentum/node_modules/appium/build/lib/main.js";
            } else if (osName.contains("linux")) {
//            linux path
                Appium_Node_Path = System.getenv("HOME") + "/.linuxbrew/bin/node";
                Appium_JS_Path = System.getenv("HOME") + "/.linuxbrew/lib/node_modules/appium/build/lib/main.js";
            }


            //Build the Appium service
            service = AppiumDriverLocalService.buildService(new AppiumServiceBuilder()
                    .withIPAddress("127.0.0.1")
                    .usingPort(4723).usingDriverExecutable(new File(Appium_Node_Path))
                    .withAppiumJS(new File(Appium_JS_Path))
                    //do not display the session log in console
                    .withArgument(GeneralServerFlag.SESSION_OVERRIDE)
                    .withArgument(GeneralServerFlag.LOG_LEVEL,"debug"));  //log level can be: info, error, debug
            service.start();
            System.out.println("__________Appium Server has been started!!____________");

        }

        public static void stopAppiumServer() throws Exception {
            if (service != null) {
            //System.out.println("state of service " + service.isRunning());
                service.stop();
                System.out.println("___________Appium Server has been stopped!!__________");
            }
        }

        //check if server is running first before staring new session
        public static boolean checkIfServerIsRunnning(int port) {
            boolean isServerRunning = false;
            ServerSocket serverSocket;
            try {
                serverSocket = new ServerSocket(port);
                serverSocket.close();
            } catch (IOException e) {
                //If control comes here, then it means that the port is in use
                isServerRunning = true;
            } finally {
                serverSocket = null;
            }
            return isServerRunning;
        }


    }
