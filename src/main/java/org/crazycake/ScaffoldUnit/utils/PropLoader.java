package org.crazycake.ScaffoldUnit.utils;

import java.io.InputStream;
import java.util.Properties;

import org.crazycake.ScaffoldUnit.ScaffoldUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropLoader {
    
    private static String url;
    private static String username;
    private static String password;
    private static String type;
    
    private static Logger logger = LoggerFactory.getLogger(PropLoader.class);
    
    static{
        Properties prop = new Properties();
        InputStream inputStream = ScaffoldUnit.class.getClassLoader().getResourceAsStream("ScaffoldUnit.properties");
        try {
            prop.load(inputStream);
            
            url = prop.getProperty("ScaffoldUnit.jdbc.url");
            logger.debug("ScaffoldUnit.jdbc.url="+url);
            
            username = prop.getProperty("ScaffoldUnit.jdbc.username");
            logger.debug("ScaffoldUnit.jdbc.username="+username);
            
            password = prop.getProperty("ScaffoldUnit.jdbc.password");
            logger.debug("ScaffoldUnit.jdbc.password="+password);
            
            type = prop.getProperty("ScaffoldUnit.jdbc.type");
            logger.debug("ScaffoldUnit.jdbc.type="+type);
            
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static String getUrl() {
        return url;
    }

    public static String getUsername() {
        return username;
    }

    public static String getPassword() {
        return password;
    }
    
    public static String getType() {
        return type;
    }
}
