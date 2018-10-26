package com.pancentric.utilities;

import org.zaproxy.clientapi.core.*;
import org.zaproxy.clientapi.core.Alert.Confidence;
import org.zaproxy.clientapi.core.Alert.Risk;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

public class SecurityScanner {

    private static final String ZAP_ADDRESS = "source.cloud.infomentum.co.uk";
    private static int ZAP_PORT = 2375;
    private static final String ZAP_API_KEY = null; // Change this if you have set the apikey in ZAP via Options / API
    //private static final String TARGET = System.getProperty("URLTOTEST");
    private static final ClientApi api = new ClientApi(ZAP_ADDRESS, ZAP_PORT);

    public void createScanSession(String baseUrl, String sessionName, String contextName, List<String> includeContext, List<String> excludeContext) throws Exception {
        System.out.println("ZAP PORT" + ZAP_PORT);
        System.out.println("ZAP ADDRESS" + ZAP_ADDRESS);
        // create new session
        System.out.println("Creating new session");
        api.core.newSession(ZAP_API_KEY, "target/"+sessionName+".session", "true");

        // create new context
        System.out.println("Creating new context");
        api.context.newContext(ZAP_API_KEY, contextName);

        // set include in context
        System.out.println("Creating new scope");
        System.out.println("TARGET_URL"+baseUrl);

        // set context include list
        for (String contextUrl : includeContext) {
            api.addIncludeInContext(ZAP_API_KEY, contextName, baseUrl + contextUrl);
        }

        // set context exclude list
        for (String contextUrl : excludeContext) {
            api.addExcludeFromContext(ZAP_API_KEY, contextName, contextUrl);
        }

        // remove the default context
        api.context.removeContext(ZAP_API_KEY, "Default Context");

        // tests will now be run through the proxy ...
        // ...
    }

    public List<String> startActiveScan(String baseUrl, String sessionName) throws Exception {

        //api.core.saveSession(ZAP_API_KEY,sessionName, "true");
        // set the active session
        System.out.println("Setting active session");
        api.httpSessions.setActiveSession(ZAP_API_KEY, baseUrl.replace("https://", "") + ":443", "Session 0");
        List<String> alertDetails = new ArrayList<String>();

        try {
            String scanid;
            int progress;

            System.out.println("Active scan : " + baseUrl);
            ApiResponse resp = api.ascan.scan(ZAP_API_KEY, baseUrl, "True", "True", null, null, null);

            // The scan now returns a scan id to support concurrent scanning
            scanid = ((ApiResponseElement) resp).getValue();

            // Poll the status until it completes
            while (true) {
                Thread.sleep(5000);
                progress = Integer.parseInt(((ApiResponseElement) api.ascan.status(scanid)).getValue());
                System.out.println("Active Scan progress : " + progress + "%");
                if (progress >= 100) {
                    break;
                }
            }

            System.out.println("Active HTTP Scan complete");

            //System.out.println("Alerts:");
            //System.out.println(new String(api.core.xmlreport(ZAP_API_KEY)));

            byte[] report = api.core.htmlreport(ZAP_API_KEY);
            FileOutputStream fos = new FileOutputStream("target/security-report.html");
            fos.write(report);
            fos.close();

            //ApiResponse respj = api.core.alerts(baseUrl, null, null);
            //ApiResponseList test = ((ApiResponseList) respj);
            //List<ApiResponse> list = test.getItems();

            //int i = 0;

            //System.out.println("NUMBER OF ERRORS: " + list.size());

//            Map<String,String> errors = new HashMap<String,String>();
//            while (i < list.size()) {
//                ApiResponse alertresponse = list.get(i);
//                ApiResponseSet setresult = ((ApiResponseSet) alertresponse);
//                if (errors.containsKey(setresult.getAttribute("alert"))==false) {
//                    errors.put(setresult.getAttribute("alert"), setresult.getAttribute("solution"));
//                    System.out.println(setresult.getAttribute("alert"));
//
//                    System.out.println(setresult.getAttribute("solution"));
//                    System.out.println(setresult.getAttribute("count"));
//                    System.out.println(setresult.getAttribute("riskdesc"));
//                    System.out.println(setresult.getAttribute("uri"));
//                    System.out.println(setresult.getAttribute("desc"));
//                    System.out.println(setresult.getAttribute("confidence"));
//
//                }
//                i++;
//            }

            List<Alert> alerts = new ArrayList<Alert>();
            List<String> uniqueAlerts = new ArrayList<String>();
            ApiResponse response = api.core.alerts(baseUrl, null, null);
            int counter = 0;

            if (response != null && response instanceof ApiResponseList) {
                ApiResponseList alertList = (ApiResponseList) response;
                for (ApiResponse alertresp : alertList.getItems()) {
                    ApiResponseSet alertSet = (ApiResponseSet) alertresp;
                    if (uniqueAlerts.contains(alertSet.getAttribute("alert")) == false) {

                        counter++;

                        alerts.add(new Alert(
                                alertSet.getAttribute("alert"),
                                alertSet.getAttribute("url"),
                                Risk.valueOf(alertSet.getAttribute("risk")),
                                Confidence.valueOf(alertSet.getAttribute("confidence")),
                                alertSet.getAttribute("param"),
                                alertSet.getAttribute("other"),
                                alertSet.getAttribute("attack"),
                                alertSet.getAttribute("description"),
                                alertSet.getAttribute("reference"),
                                alertSet.getAttribute("solution"),
                                alertSet.getAttribute("evidence"),
                                Integer.parseInt(alertSet.getAttribute("cweid")),
                                Integer.parseInt(alertSet.getAttribute("wascid"))));

                        // add thr unique alert reference
                        uniqueAlerts.add((alertSet.getAttribute("alert")));

                        // add the alert details
                        alertDetails.add("\n\r" + counter + ") Alert: " + alertSet.getAttribute("alert") + "\n\r" +
                                "Risk: " + alertSet.getAttribute("alert") + "\n\r" +
                                "URL: " + alertSet.getAttribute("url") + "\n\r" +
                                "Description: " + alertSet.getAttribute("description") + "\n\r" +
                                "Soluton: " + alertSet.getAttribute("solution") + "\n\r" +
                                "Reference: " + alertSet.getAttribute("reference") + "\n\r" +
                                "Evidence: " + alertSet.getAttribute("evidence") + "\n\r" +
                                "=========================================================================================\n\r");
                    }
                }
            }

            //System.out.println(alerts.size());

//            try {
//                api.core.saveSession(ZAP_API_KEY,sessionName+".session", "true");
//                System.out.println( "Session save successful (target/"+sessionName+".session)." );
//            }
//            catch (ClientApiException ex) {
//                System.out.println( "Error saving session." );
//                ex.printStackTrace();
//            }
        } catch (ClientApiException ex) {
            System.out.println("Error executing scan");
            ex.printStackTrace();
        }

        return alertDetails;

    }


    //ApiResponse respj = api.core.alerts(TARGET, null, null);
    //ApiResponseList test = ((ApiResponseList) respj);
    //List<ApiResponse> list = test.getItems();

    //int i = 0;
            /*
            System.out.println("NUMBER OF ERRORS: " + list.size());

            Map<String,String> errors = new HashMap<String,String>();
            while (i < list.size()) {
                ApiResponse alertresponse = list.get(i);
                ApiResponseSet setresult = ((ApiResponseSet) alertresponse);
                if (errors.containsKey(setresult.getAttribute("alert"))==false) {
                    errors.put(setresult.getAttribute("alert"), setresult.getAttribute("solution"));
                    System.out.println(setresult.getAttribute("alert"));



                    System.out.println(setresult.getAttribute("solution"));
                    System.out.println(setresult.getAttribute("count"));
                    System.out.println(setresult.getAttribute("riskdesc"));
                    System.out.println(setresult.getAttribute("uri"));
                    System.out.println(setresult.getAttribute("desc"));
                    System.out.println(setresult.getAttribute("confidence"));

                }
                i++;
            }
            */

    public void spriderUrl(String baseUrl, String contextname) throws Exception {

        try {
            String spiderid;
            int progress;

            System.out.println("Spider : " + baseUrl);
            ApiResponse resp = api.spider.scan(ZAP_API_KEY,baseUrl.replace("https://", "")+":443", "3","false",contextname,"true");

            // The scan now returns a scan id to support concurrent scanning
            spiderid = ((ApiResponseElement) resp).getValue();

            // Poll the status until it completes
            while (true) {
                Thread.sleep(5000);
                progress = Integer.parseInt(((ApiResponseElement) api.spider.status(spiderid)).getValue());
                System.out.println("Spider progress : " + progress + "%");
                if (progress >= 100) {
                    break;
                }
            }

            System.out.println("Spider complete");
        }

        catch (ClientApiException ex) {
            System.out.println( "Error running spider" );
            ex.printStackTrace();
        }
    }
}