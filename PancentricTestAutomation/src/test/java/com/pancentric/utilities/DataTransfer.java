package com.pancentric.utilities;



import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import java.util.Vector;
import java.util.List;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.util.ArrayList;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;


public class DataTransfer {
/*
        // get the connection details for the SFTP host
        private String sftpHost;
        private int sftpPort;
        private String sftpUser;
        private String sftpPassword;
        private String sftpWorkingDirectory;
        private String sftpArchiveDirectory;
        private String sftpErrorDirectory;
        private String localFilePath;
        // initiate error hospital actions
        private ErrorHospitalActions errorHospital;
        public List<String> erroredFiles;
        private static SpectrumStepDefinitions stepDefs;

        public DataTransfer(SpectrumStepDefinitions StepDefs) {
            stepDefs = StepDefs;
        }
        
        /**
         * Upload a whole directory (including its nested sub directories and files)
         * to a FTP server.
         *
         * @param ftpClient
         *            an instance of org.apache.commons.net.ftp.FTPClient class.
         * @param remoteDirPath
         *            Path of the destination directory on the server.
         * @param localParentDir
         *            Path of the local directory being uploaded.
         * @param remoteParentDir
         *            Path of the parent directory of the current directory on the
         *            server (used by recursive calls).
         * @throws IOException
         *             if any network or IO error occurred.
         */
        /*
        public static void uploadDirectory(FTPClient ftpClient,
                String remoteDirPath, String localParentDir, String remoteParentDir)
                throws IOException {
         
            System.out.println("LISTING directory: " + localParentDir);
         
            File localDir = new File(localParentDir);
            File[] subFiles = localDir.listFiles();
            if (subFiles != null && subFiles.length > 0) {
                for (File item : subFiles) {
                    String remoteFilePath = remoteDirPath + "/" + remoteParentDir
                            + "/" + item.getName();
                    if (remoteParentDir.equals("")) {
                        remoteFilePath = remoteDirPath + "/" + item.getName();
                    }
         
                    if (item.isFile()) {
                        // upload the file
                        String localFileFullPath = item.getAbsolutePath();
                        System.out.println("About to upload the file: " + localFileFullPath);
                        boolean uploaded = uploadSingleFile(ftpClient,
                                localFileFullPath, remoteFilePath);
                        if (uploaded) {
                            System.out.println("UPLOADED a file to: "
                                    + remoteFilePath);
                        } else {
                            System.out.println("COULD NOT upload the file: "
                                    + localFileFullPath);
                        }
                    } else {
                        // create directory on the server
                        boolean created = ftpClient.makeDirectory(remoteFilePath);
                        if (created) {
                            System.out.println("CREATED the directory: "
                                    + remoteFilePath);
                        } else {
                            System.out.println("COULD NOT create the directory: "
                                    + remoteFilePath);
                        }
         
                        // upload the sub directory
                        String parent = remoteParentDir + "/" + item.getName();
                        if (remoteParentDir.equals("")) {
                            parent = item.getName();
                        }
         
                        localParentDir = item.getAbsolutePath();
                        uploadDirectory(ftpClient, remoteDirPath, localParentDir,
                                parent);
                    }
                }
            }
        }
        
    /**
     * Upload a single file to the FTP server.
     *
     * @param ftpClient
     *            an instance of org.apache.commons.net.ftp.FTPClient class.
     * @param localFilePath
     *            Path of the file on local computer
     * @param remoteFilePath
     *            Path of the file on remote the server
     * @return true if the file was uploaded successfully, false otherwise
     * @throws IOException
     *             if any network or IO error occurred.
     */
    
    // a method to retreive the connection details for the environment
    /*
    public void setFtpConnectionDetails(String filepath, String prefix, String suffix) throws Exception {
        
        // locate the appropriate prop file for the client type
        PropertyFetcher propertyfile = new PropertyFetcher();
        String propfile = "environment/"+SpectrumStepDefinitions.environment;
        
        // get the connection details for the SFTP host
        sftpHost = propertyfile.getPropValues(propfile, prefix+"SftpHost");
        sftpPort = Integer.parseInt(propertyfile.getPropValues(propfile, prefix+"SftpPort"));
        sftpUser = propertyfile.getPropValues(propfile, prefix+"SftpUser");
        sftpPassword = propertyfile.getPropValues(propfile, prefix+"SftpPassword");
        sftpWorkingDirectory = propertyfile.getPropValues(propfile, prefix+"SftpWorkingDirectory"+suffix);
        sftpArchiveDirectory = propertyfile.getPropValues(propfile, prefix+"SftpArchiveDirectory"+suffix);
        sftpErrorDirectory = propertyfile.getPropValues(propfile, prefix+"SftpErrorDirectory"+suffix);
        localFilePath = "target/generated-data/"+filepath;
    }
    
    public static boolean uploadSingleFile(FTPClient ftpClient,
            String localFilePath, String remoteFilePath) throws IOException {
        File localFile = new File(localFilePath);
     
        InputStream inputStream = new FileInputStream(localFile);
        try {
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            return ftpClient.storeFile(remoteFilePath, inputStream);
        } finally {
            inputStream.close();
        }
    }
    
    // to transfer file onto remote target host via SFTP
    
    public boolean secureUploadSingleFile(String localFileFullPath) throws Exception {
    
        boolean transfer = false;
               
        Session session = null;
        Channel channel = null;
        ChannelSftp channelSftp = null;
        System.out.println("preparing the host information for sftp.");
        
        try {
            // try to initiate a new SFTP session
            JSch jsch = new JSch();
            session = jsch.getSession(sftpUser, sftpHost, sftpPort);
            session.setPassword(sftpPassword);
            
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.connect();
            System.out.println("Host connected.");
            channel = session.openChannel("sftp");
            channel.connect();
            System.out.println("sftp channel opened and connected.");
            channelSftp = (ChannelSftp) channel;
            channelSftp.cd(sftpWorkingDirectory);
        
            File f = new File(localFileFullPath);
            channelSftp.put(new FileInputStream(f), f.getName());
            
            // Wait for the data to be loaded on onto the target folder
            StopWatch fileUploadTimer = new StopWatch();
            fileUploadTimer.start();
            waitForFilePresent(channelSftp,f.getName(),sftpWorkingDirectory,20,0.2);
            fileUploadTimer.stop();
            
            // write to cucumber report
            SpectrumStepDefinitions.scenarioWrite(f.getName() + " transfered to " + sftpWorkingDirectory + " in " + fileUploadTimer.getElapsedTime() + " ms");
            
            // wait for the file to be processed by ODI process (should disapear from inbound folder)
            // if successful will be loaded into archive folder. if fail, error folder
            StopWatch fileProcessTimer = new StopWatch();
            fileProcessTimer.start();
            waitForFileNotPresent(channelSftp,f.getName(),sftpWorkingDirectory,30,0.2);
            String result = validateFileProcessing(channelSftp,f.getName(),sftpArchiveDirectory,sftpErrorDirectory,60,0.2);
            fileProcessTimer.stop();
            
            // use the result to set the next action
            String destinationPath = "";
            String errorText = "";
            if (result.equals("Archive")) {
                destinationPath=sftpArchiveDirectory;
                transfer=true;
            }
            else if (result.equals("Error")) {
                destinationPath=sftpErrorDirectory;                
                // get the error text from the error hospital
                errorHospital = new ErrorHospitalActions(stepDefs);
                errorText = errorHospital.getErrorCode(f.getName());                
                transfer=false;
            }
            else {
                transfer=false;
            }
            // write to cucumber report
            SpectrumStepDefinitions.scenarioWrite(result + "("+errorText+"): " + f.getName() + " transfered to " + destinationPath + "in " + fileProcessTimer.getElapsedTime() + " ms");
                
        } catch (JSchException ex) {
             System.out.println("Exception found while tranfer the response.");
             transfer = false;
        }
        finally{

            channelSftp.exit();
            System.out.println("sftp Channel exited.");
            channel.disconnect();
            System.out.println("Channel disconnected.");
            session.disconnect();
            System.out.println("Host Session disconnected.");
        }
        return transfer;
    }
    
    public List<String> secureUploadDirectory(String filepath, String prefix, String suffix) throws Exception {
        
        setFtpConnectionDetails(filepath,prefix,suffix);
        
        System.out.println("LISTING directory: " + localFilePath);
        List<String> loadedFiles = new ArrayList<String>();
        erroredFiles = new ArrayList<String>();
     
        File localDir = new File(localFilePath);
        File[] subFiles = localDir.listFiles();
        if (subFiles != null && subFiles.length > 0) {
            for (File item : subFiles) {
                String remoteFilePath = sftpWorkingDirectory + "/" + item.getName();
                if (item.isFile()) {
                    // upload the file
                    String localFilePath = item.getAbsolutePath();
                    System.out.println("About to upload the file: " + localFilePath);
                    boolean uploaded = secureUploadSingleFile(localFilePath);
                    if (uploaded) {
                        System.out.println("UPLOADED a file to: "+ remoteFilePath);
                        loadedFiles.add(item.getName());
                        
                    } else {
                        System.out.println("COULD NOT upload the file: "
                                + localFilePath);
                        erroredFiles.add(item.getName());
                    }
                }
            }
        }
        return loadedFiles;
    }
    
    // a metehod to wait for a file to be present in a certain directory
    
    public static boolean waitForFilePresent(ChannelSftp channelSftp, String filename, String filepath, int timeout, double interval) throws Exception {
                
        interval = interval * 1000;
        int pause = (int) interval;
        long end = System.currentTimeMillis() + (timeout * 1000);
        boolean result = false;
        
        channelSftp.cd(filepath);
        
        while ((System.currentTimeMillis() < end) && (result == false)) {
            
            Vector<ChannelSftp.LsEntry> list = channelSftp.ls(filepath);
            for(ChannelSftp.LsEntry entry : list) {
                if (entry.getFilename().contains(filename)) {
                    System.out.println("file found");
                    result=true;
                    break;
                }
                else {
                    System.out.println("File not found this time");
                }
            }     
            Thread.sleep(pause);    
        }
        
        // throw exception if the file is not found in the directory
        if (result==false) {
            NoSuchElementException e = new NoSuchElementException("File not transfered within " + timeout + " seconds.");
            byte[] myvar = "Screenshot not available".getBytes();
            stepDefs.handleException(e, myvar);
            throw e;
        }
        return result;
    }
    
    //TODO a method to see if an errored file has been resubmitted
    
    public boolean checkForResubmission() throws Exception {
        
        return true;
    }
    
    // a method to wait for a file to not be present in a certain directory
    
    public static boolean waitForFileNotPresent(ChannelSftp channelSftp, String filename, String filepath, int timeout, double interval) throws Exception {
                
        channelSftp.cd(filepath);
        
        interval = interval * 1000;
        int pause = (int) interval;
        long end = System.currentTimeMillis() + (timeout * 1000);
        boolean result = false;
        
        while ((System.currentTimeMillis() < end) && (result == false)) {
            result=true;
            Vector<ChannelSftp.LsEntry> list = channelSftp.ls(filepath);
            for(ChannelSftp.LsEntry entry : list) {
                if (entry.getFilename().contains(filename)) {
                    System.out.println("File still found this time");
                    result=false;
                }
                else {
                    System.out.println("File now removed");
                
                }
                if (result==true) {
                    break;
                }
            }
            Thread.sleep(pause);
        }
        
        // throw exception if the file is not found in the directory
        if (result==false) {
            NoSuchElementException e = new NoSuchElementException("File not processed within " + timeout + " seconds.");
            byte[] myvar = "Screenshot not available".getBytes();
            stepDefs.handleException(e, myvar);
            throw e;
        }
        return result;
    }
    
    // a method to validate the appearance of a file in one folder or the other
    
    public static String validateFileProcessing(ChannelSftp channelSftp,String filename, String archivepath, String errorpath, int timeout, double interval) throws Exception {
        interval = interval * 1000;
        int pause = (int) interval;
        long end = System.currentTimeMillis() + (timeout * 1000);
        String result = "NotFound";
        
        error_or_archive:
        while ((System.currentTimeMillis() < end) && (result == "NotFound")) {
            
            channelSftp.cd(archivepath);
            Vector<ChannelSftp.LsEntry> list = channelSftp.ls("*"+filename);
            for(ChannelSftp.LsEntry entry : list) {
                // TODO review if contains logic will work
                if (entry.getFilename().contains(filename)) {
                    System.out.println("file found in archive dir");
                    result="Archive";
                    filename=entry.getFilename();
                    break error_or_archive;
                }
                else {
                    
                }
            }
            
            // check in the error path
            channelSftp.cd(errorpath); 
            list = channelSftp.ls("*"+filename);
            for(ChannelSftp.LsEntry entry : list) {
                System.out.println(entry.getFilename());
                if (entry.getFilename().contains(filename)) {
                    System.out.println("File found in error dir");
                    result="Error";
                    filename=entry.getFilename();
                    break error_or_archive;
                }
                else {
                    
                }
            }
            
            Thread.sleep(pause);
        }
        
        // throw exception if the file is not found in the directory
        if (result=="NotFound") {
            NoSuchElementException e = new NoSuchElementException("File not processed within " + timeout + " seconds.");
            byte[] myvar = "Screenshot not available".getBytes();
            stepDefs.handleException(e, myvar);
            //throw e;
        }
        System.out.println(result);
        return result;
    }
*/
}
