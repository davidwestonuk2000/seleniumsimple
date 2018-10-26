package com.pancentric.utilities;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
 
public class PropertyFetcher {
	String result = "";
	InputStream inputStream;
 
	public String getPropValues(String propfile, String property) throws IOException {
 
		try {
		    Properties prop = new Properties();
			String propFileName = "com/pancentric/" + propfile + ".properties";
                            inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);                    
			if (inputStream != null) {
				prop.load(inputStream);
			} else {
                            throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");    
			}
 
			// get the property value and print it out
			result = prop.getProperty(property);
		} catch (Exception e) {
			System.out.println("Exception: " + e);
			throw e;
		} finally {
                inputStream.close();
		}
		return result;
	}
        
    public  void updateProperty(String propfile, String property, String newValue) throws Exception {
        
    try {
            Properties prop = new Properties();
            String propFileName = "com/infomentum/" + propfile + ".properties";
            inputStream = getClass().getClassLoader().getResourceAsStream(propFileName); 
    
            if (inputStream != null) {
                    System.out.println("here");
                    prop.load(inputStream);
            } else {
                    throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
            }
            String outFileName="src/test/resources/com/infomentum/" + propfile + ".properties";
            // get the property value and print it out
            inputStream.close();
            FileOutputStream out = new FileOutputStream(outFileName);
            prop.setProperty(property, newValue);
            prop.store(out, null);
            out.close();
           
    } catch (Exception e) {
            System.out.println("Exception: " + e);
    } finally {
            inputStream.close();
    }
    
    }
        
    public HashMap<String,String[]> getAllProperties(String propfile) throws IOException {
            HashMap<String,String[]> fields = new HashMap();
            try {
                    Properties prop = new Properties();
                    String propFileName = "com/infomentum/" + propfile + ".properties";
                    inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
    
                    if (inputStream != null) {
                            prop.load(inputStream);
                    } else {
                            throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
                    }
                    // get all of the properties in the file
                        
                        Enumeration names = prop.propertyNames();
                            while (names.hasMoreElements()) {
                                String key = ((String) names.nextElement());
                                String value = (prop.get(key)).toString();
                                fields.put((key),value.split(","));
                            }                
                    
            } catch (Exception e) {
                    System.out.println("Exception: " + e);
            } finally {
                    inputStream.close();
            }
            return fields;
    }
}
