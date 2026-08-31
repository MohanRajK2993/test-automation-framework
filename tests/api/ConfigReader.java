package org.practice.Junit5;

import java.io.FileInputStream;
import java.util.Properties;

public class ConfigReader {
    private static Properties properties;
    
    static {
        try {
            String configPath = System.getProperty("user.dir") + 
                               "/src/test/resources/config.properties";
            FileInputStream fileInput = new FileInputStream(configPath);
            properties = new Properties();
            properties.load(fileInput);
            fileInput.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
    
    public static String getBaseURL() {
        return getProperty("baseURL");
    }
    
    public static String getAuthToken() {
        return getProperty("authToken");
    }
    
    public static String getUsername() {
        return getProperty("authUsername");
    }
    
    public static String getPassword() {
        return getProperty("authPassword");
    }
}