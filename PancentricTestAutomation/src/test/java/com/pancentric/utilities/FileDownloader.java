package com.pancentric.utilities;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
//import java.time.*;




import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;


import org.apache.http.client.HttpClient;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class FileDownloader {

    public static String fileDownloader(WebDriver driver, WebElement element, String downloadPath, String fileName, Integer timeout) throws Exception {
    	
    	String result = "";
     	
    	// This will grab the URL to download from the web element in question.
    	// Web element comes from the objectLocator.
        String urlHREF = element.getAttribute("href");
       // Throw an exception if there is no referenced URL.
        if (urlHREF.trim().equals("")) {
            throw new Exception("The element you have specified does not link to anything!");
        }
        
        
        //URL downloadURL = new URL(urlHREF);
        // using sun handler for http protocols
       URL downloadURL = new URL(null, urlHREF, new sun.net.www.protocol.https.Handler());
        //Explicitly opening the connection incase it does fire up; if your site is https please make sure to add the 
       //certificate to the demotrust.jks and also to the keystore.
        HttpURLConnection con = (HttpURLConnection) downloadURL.openConnection();
       //DefaultHttpClient is now deprecated 
        HttpClient client = new DefaultHttpClient();
        
        // We need to get the cookies from the webDriver session, so any authentication is replicated accurately.
        client = setCookies((DefaultHttpClient)client, driver);   
        HttpGet getRequest = new HttpGet(downloadURL.toURI());
                  
        try {
            System.out.println("Inside try loop of ");
               
              //  del.setHeader("content-type", "application/json");
              con.setRequestMethod("GET");
              con.setConnectTimeout(timeout);

              //this.trace("Response Code: " + con.getResponseCode());
           // HttpResponse myResponse = client.execute(getRequest);
           
             int status = con.getResponseCode();
            //int status = myResponse.getStatusLine().getStatusCode();
            System.out.println("Status_Code" + status);
            // Check to make sure that the get request has succeeded,
            // fail if we get a status other than HTTP_OK removed the hard-coded 200. 
            if (status != HttpURLConnection.HTTP_OK) {
                return "fail: " + status;
            }
                  
            result = fileWriter(downloadPath, fileName, (HttpResponse)con.getInputStream());

        } catch (Exception Ex) {
            throw new Exception(Ex.getCause());
        } finally {
            getRequest.abort();
        }       
        return result;
    }
    
    public static String fileDownloaderNonBrowser(String urlHREF, String downloadPath, String fileName, Integer timeout) throws Exception {
        
        String result = "";
        
        
        // Throw an exception if there is no referenced URL.
        if (urlHREF.trim().equals("")) {
            throw new Exception("The element you have specified does not link to anything!");
        }

        URL downloadURL = new URL(urlHREF); 
                
        HttpClient client = new DefaultHttpClient();
        HttpGet getRequest = new HttpGet(downloadURL.toURI());
             
        try {
            HttpResponse myResponse = client.execute(getRequest);
            
            int status = myResponse.getStatusLine().getStatusCode();
            // Check to make sure that the get request has succeeded,
            // fail if we get a status other than 200. 
            if (status != 200) {
                System.out.println(status);
                System.out.println(myResponse.getStatusLine());
                return "fail: " + myResponse.getStatusLine();
            }
                  
            result = fileWriter(downloadPath, fileName, myResponse);
            System.out.println("written file to " +downloadPath);

        } catch (Exception Ex) {
            throw new Exception(Ex.getCause());
        } finally {
            getRequest.abort();
        }       
        return result;
    }

    // Retrieve the cookies from the webDriver session and place them in a cookie store array.
    // Attach this array to the client, so any requests will be properly authenticated.
    private static DefaultHttpClient setCookies(DefaultHttpClient client, WebDriver driver) {
    	
        client.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.RFC_2965);
        
        CookieStore cookieStore = new BasicCookieStore();
        
        Set<org.openqa.selenium.Cookie> driverCookieSet = driver.manage().getCookies(); 
        
        for (org.openqa.selenium.Cookie myCookie : driverCookieSet) {
        	BasicClientCookie newCookie = new BasicClientCookie(myCookie.getName(), myCookie.getValue());
        	newCookie.setDomain(myCookie.getDomain());
        	newCookie.setPath(myCookie.getPath());
        	newCookie.setSecure(myCookie.isSecure());         
            cookieStore.addCookie(newCookie);
            }
        
        client.setCookieStore(cookieStore);

    	return client;
    }    
    
    
    private static String fileWriter(String downloadPath, String fileName, HttpResponse myResponse) {
   	
    	try {
   	
	        // Read in the content and write it out one line at a time.
    		InputStream rd = myResponse.getEntity().getContent();
    		//String fileName = getFileName(myResponse);
	        FileOutputStream fout = new FileOutputStream(new File(downloadPath + fileName));
	        System.out.println(downloadPath + fileName);
	        byte[] b = new byte[8192];
	        int bytesRead;
	           while (true) {
	            bytesRead = rd.read(b);
	               // System.out.println("bytesRead = "+bytesRead );
	               if (bytesRead==-1) { 
	            	   break;
	               }
	               fout.write(b, 0, bytesRead);
	           }        
	        
	        fout.flush();
	        fout.close();
	        // Close down our reader and writer.
	        rd.close();
	       
    	} catch (Exception ex) {
    		return "fail: " + ex;
    	}
        
        return "pass";
    }

    // Retrieves the file name portion of the server response.
    // Separated, as we may wish to provide a custom file name at a later date.
    private static String getFileName(HttpResponse myResponse) {
    	
    	String fileName = "";
    	
        // Obtain the name of the file from the header.
        // Use this name for writing the file.
        for (Header header : myResponse.getAllHeaders()) {
        	HeaderElement[] helelms = header.getElements();
        	if (helelms.length > 0) {
        	    HeaderElement helem = helelms[0];
        	    if (helem.getName().equalsIgnoreCase("inline")) {
        	        NameValuePair nmv = helem.getParameterByName("filename");
        	        if (nmv != null) {
        	            fileName = nmv.getValue();
        	        }
        	}
        }
            if (fileName.length() < 1) {
                fileName = "temp.pdf";
            }
        }
        return fileName;    
    }
    
    
    
    
   //Get the latest downloaded file name 
    public File getLatestFilefromDir(String dirPath){
                File dir = new File(dirPath);
                File[] files = dir.listFiles();
                if (files == null || files.length == 0) {
                    return null;
                }
            
                File lastModifiedFile = files[0];
                for (int i = 1; i < files.length; i++) {
                   if (lastModifiedFile.lastModified() < files[i].lastModified()) {
                       lastModifiedFile = files[i];
                   }
                }
                return lastModifiedFile;
    }
    
  
    
  
    //Is file downloaded with in last few min  
    public static boolean getLastModified(String path) throws IOException{
      File dir = new File(path);

      File[] files = dir.listFiles();
      if (files.length == 0) {
          throw new IOException("No files to monitor in the dir");
      }

      Date modifiedDate = null;
      File lastModifiedFile = files[0];
      for (int i = 1; i < files.length; i++) {
         if (lastModifiedFile.lastModified() < files[i].lastModified()) {
              modifiedDate = new Date(files[i].lastModified());
         }
      }
      Date currentDate = new Date();
      Calendar cal = Calendar.getInstance();
      cal.setTime(currentDate);
      cal.add(Calendar.MINUTE, -2);
      Date alertDate = cal.getTime();
      System.out.println("AlertDate >" + alertDate);
      System.out.println("modifiedDate >" + modifiedDate);

      if (modifiedDate != null && modifiedDate.after(alertDate)){
          return true;
      } else {
          return false;
      }
  }
}