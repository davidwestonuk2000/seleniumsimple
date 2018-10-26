package com.pancentric.utilities;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyReader {

    private Properties properties = new Properties();
    private InputStream inputStream = null;
    private String path = "src/test/resources/com/infomentum/" + System.getProperty("project") + "/config/config.properties";


    public PropertyReader() {
        loadProperties();
    }


    private void loadProperties() {
        try {
            inputStream = new FileInputStream(path);
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public String readProperty(String key) {
        return properties.getProperty(key);
    }
}
