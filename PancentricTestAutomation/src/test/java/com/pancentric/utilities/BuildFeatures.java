package com.pancentric.utilities;

public class BuildFeatures {

    private static String projectKey = "BF";//System.getProperty("PROJECT_KEY");
    //private static String issueType = (System.getProperty("ISSUE_TYPE"));
     
    public static void main(String[] args) throws Exception {

        try {
            System.out.println(projectKey);
            com.pancentric.utilities.TestResults.buildFeatureFiles(projectKey, "");
            System.exit(0);
        }
    
        catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println(e.getStackTrace().toString());
            System.out.println("Unable to update scenarios");
            System.exit(0);
        }
    }
}