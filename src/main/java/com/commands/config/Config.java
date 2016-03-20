package com.commands.config;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

/**
 * Created by Pablo on 3/20/2016.
 */
public class Config {

    private static Config instance;
    private static Properties prop;
    protected Config(){
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            File file = new File(classLoader.getResource("config.properties").getFile());
            FileInputStream fileInput = new FileInputStream(file);
            prop = new Properties();
            prop.load(fileInput);
            fileInput.close();
        } catch (Exception e) {
            System.out.println("Exception: " + e);
        }
    }

    public static Config getInstance(){
        if( instance  == null ){
            instance = new Config();
        }
        return instance;
    }

    /**
     *
     * @param key: name of the property
     * @return String with the property value if exists, otherwise Null
     */
    public String getProperty(String key){
        if( prop != null ){
            return prop.getProperty(key);
        }
        return null;
    }

}
