package com.pancentric.jirahelper;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import java.net.InetAddress;
import sun.misc.BASE64Encoder;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.json.JSONArray;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import java.io.File;
import java.nio.charset.StandardCharsets;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.content.FileBody;

/**
 * Demonstrates how to update a page using the Confluence 5.5 REST API.
 */
public class ConfluenceHelper {
    private static final String BASE_URL = "https://confluence.XXXX.com";
    private static final String USERNAME = "XXXX";
    private static final String PASSWORD = "XXXX";
    private static final String ENCODING = "utf-8";

    private static String getContentRestUrl(final Long contentId) throws UnsupportedEncodingException {
        return String.format("%s/rest/api/content/%s/child/attachment?start=0&limit=10&os_authType=basic&os_username=%s&os_password=%s", BASE_URL, contentId, URLEncoder.encode(USERNAME, ENCODING), URLEncoder.encode(PASSWORD, ENCODING));
    }
    
    private static String getContentRestUrlExistingUpload(final Long contentId, String attachmentId) throws UnsupportedEncodingException {
        return String.format("%s/rest/api/content/%s/child/attachment/%s/data?os_authType=basic&os_username=%s&os_password=%s", BASE_URL, contentId, attachmentId, URLEncoder.encode(USERNAME, ENCODING), URLEncoder.encode(PASSWORD, ENCODING));
    }
    
    private static String getContentRestUrlNewUpload(final Long contentId) throws UnsupportedEncodingException {
        return String.format("%s/rest/api/content/%s/child/attachment?os_authType=basic&os_username=%s&os_password=%s", BASE_URL, contentId, URLEncoder.encode(USERNAME, ENCODING), URLEncoder.encode(PASSWORD, ENCODING));
    }
    
    // for downloading all attachments from a confluence page

    public void downloadPageAttachments(final long pageId, String project_key) throws Exception {
    
        JSONArray results = getPageAttachments(pageId);
        String target = setTargetFolder(project_key);
        
        // download the files to the target folder
        int i = 0;
        FileDownloader downloader = new FileDownloader();
        while (i < results.length()) {
            JSONObject result = results.getJSONObject(i);            
            JSONObject links = result.getJSONObject("_links");
            downloader.fileDownloaderNonBrowser((BASE_URL + links.get("download")+"&os_authType=basic&os_username=selenium&os_password=selenium"), target, 30);
            i++;
        }
    }
        
    // a method for uploading new versions of existing files to confleunce
    
    public static void reuploadExistingFiles(final long pageId, String project_key) throws Exception {
    
        JSONArray results = getPageAttachments(pageId);
        String target = setTargetFolder(project_key);
        
        // download the files to the target folder
        int i = 0;
        while (i < results.length()) {
            JSONObject result = results.getJSONObject(i);
            uploadExistingFileToConfluence(target,result.get("title").toString(), pageId, result.get("id").toString());
            i++;
        }
    }
    
    // a method for uploading new versions of existing files to confleunce
    
    public static void uploadNewFile(final long pageId, String fileLocation) throws Exception {
        uploadNewFileToConfluence(fileLocation, pageId);
    }

    // a method for uploading new versions of existing files to confleunce

    public static String reUploadNewFile(String filePath, String fileName, String pageId, String attachmentId) throws Exception {
        Long page = Long.parseLong(pageId);
        return uploadExistingFileToConfluence(filePath, fileName, page, attachmentId);
    }
    
    // set the target folder for the download
    
    private static String setTargetFolder(String project_key) throws Exception {
        // set the target folder
        String hostname = InetAddress.getLocalHost().getHostName();
        String target = "";
        // set the path to store the feature files
        if (hostname.contains("source")) {
            target = "SeleniumTests//target//generated-data//test-data//" + project_key + "//";
        }
        else {
            target = "target//generated-data//test-data//" + project_key + "//";
        }
        return target;
    }
    
    private static JSONArray getPageAttachments(Long pageId) throws Exception {
        HttpClient client = new DefaultHttpClient();
        
        // Get current page version
        String pageObj = null;
        HttpEntity pageEntity = null;
        try
        {
            HttpGet getPageRequest = new HttpGet(getContentRestUrl(pageId));
            HttpResponse getPageResponse = client.execute(getPageRequest);
            pageEntity = getPageResponse.getEntity();

            pageObj = IOUtils.toString(pageEntity.getContent());

            System.out.println("Get Page Request returned " + getPageResponse.getStatusLine().toString());
            System.out.println("");
            System.out.println(pageObj);
        }
        finally {
            if (pageEntity != null) {
                EntityUtils.consume(pageEntity);
            }
        }

        // Parse response into JSON
        JSONObject page = new JSONObject(pageObj);
        JSONArray results = page.getJSONArray("results");
        return results;
    }
    
    private static String uploadExistingFileToConfluence(String filePath, String fileName, Long pageId, String attachmentId) throws Exception {
        String downloadUrl;
        try {
            
            HttpClient client = new DefaultHttpClient();
            
            // Get current page version
            String pageObj = null;
            HttpEntity pageEntity = null;
            
            String pathname= filePath + fileName; 
            File fileUpload = new File(pathname);

            HttpPost postAttachment = new HttpPost((getContentRestUrlExistingUpload(pageId,attachmentId)));
            BASE64Encoder base=new BASE64Encoder();
            String encoding = base.encode ((USERNAME+":"+PASSWORD).getBytes());            
            postAttachment.setHeader("Authorization", "Basic " + encoding);
            postAttachment.setHeader("X-Atlassian-Token","nocheck");
            
            final MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            builder.setCharset(StandardCharsets.UTF_8);
            builder.addPart("file", new FileBody(fileUpload));
            //builder.addTextBody("comment", "Uploaded by build" + System.getProperty("BUILD_NUMBER"), ContentType.TEXT_PLAIN.withCharset(StandardCharsets.UTF_8));
            builder.addTextBody("minorEdit", "true");

            HttpEntity entity = builder.build();
            postAttachment.setEntity(entity);
            HttpResponse response = client.execute(postAttachment);
            
            pageEntity = response.getEntity();

            pageObj = IOUtils.toString(pageEntity.getContent());
            JSONObject myObject = new JSONObject(pageObj);
            downloadUrl = myObject.getJSONObject("_links").get("download").toString();
            System.out.println("Get Page Request returned " + response.getStatusLine());
        }
        
        catch (Exception e) {
            throw e;
        }
        return downloadUrl;
    }
    
    private static void uploadNewFileToConfluence(String filePath, Long pageId) throws Exception {
        try {
            
            HttpClient client = new DefaultHttpClient();
            
            // Get current page version
            String pageObj = null;
            HttpEntity pageEntity = null;
            
            String pathname= filePath; 
            File fileUpload = new File(pathname);
            
            System.out.println(fileUpload.getName());
            
            HttpPost postAttachment = new HttpPost((getContentRestUrlNewUpload(pageId)));
            BASE64Encoder base=new BASE64Encoder();
            String encoding = base.encode ((USERNAME+":"+PASSWORD).getBytes());            
            postAttachment.setHeader("Authorization", "Basic " + encoding);
            postAttachment.setHeader("X-Atlassian-Token","nocheck");
            
            final MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            builder.setCharset(StandardCharsets.UTF_8);
            builder.addPart("file", new FileBody(fileUpload));
            builder.addTextBody("comment", "Uploaded by build" + System.getProperty("BUILD_NUMBER"), ContentType.TEXT_PLAIN.withCharset(StandardCharsets.UTF_8));
            builder.addTextBody("minorEdit", "true");

            HttpEntity entity = builder.build();
            postAttachment.setEntity(entity);
            HttpResponse response = client.execute(postAttachment);
            
            System.out.println(postAttachment.toString());
            System.out.println("RESPONSE"+response.toString());
            
            pageEntity = response.getEntity();

            pageObj = IOUtils.toString(pageEntity.getContent());

            System.out.println("Get Page Request returned " + response.getStatusLine());
            System.out.println("");
            System.out.println(pageObj);
        }    
        
        catch (Exception e) {
            throw e;
        }
    }
    
    // amend a confluence page
    
    public static void amendConfluencePage(Long pageId, String textToAdd) throws Exception {
      
                //FileDownloader downloader = new FileDownloader();
                //page.getJSONObject("body").getJSONObject("storage").put("value", "hello, world");

                //int currentVersion = page.getJSONObject("version").getInt("number");
                //page.getJSONObject("version").put("number", currentVersion + 1);

                // Send update request
                //HttpEntity putPageEntity = null;

                //try
                //{
                //    HttpPut putPageRequest = new HttpPut(getContentRestUrl(pageId, new String[]{}));

                //    StringEntity entity = new StringEntity(page.toString(), ContentType.APPLICATION_JSON);
                 //   putPageRequest.setEntity(entity);

                //    HttpResponse putPageResponse = client.execute(putPageRequest);
                //    putPageEntity = putPageResponse.getEntity();

                 //   System.out.println("Put Page Request returned " + putPageResponse.getStatusLine().toString());
                 //   System.out.println("");
                 //   System.out.println(IOUtils.toString(putPageEntity.getContent()));

               // finally
               // {
               //     EntityUtils.consume(putPageEntity);
               // }
    
        
    }
}