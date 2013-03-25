package uk.co.williammayor.simpact;

import java.util.Properties;

public class Config {
    
    private Properties properties;
    
    public Config(Properties properties) {
        this.properties = properties;
    }
    
    public int getInt(String key) throws RuntimeException {
        return getInt(properties, key);
    }

    private int getInt(Properties config, String key) throws RuntimeException {
        String value = config.getProperty(key);
        if (null != value) {
            return Integer.parseInt(value);
        } else {
            throw new RuntimeException("Config value missing: " + key);
        }
    }
    
    public float getFloat(String key) throws RuntimeException {
        return getFloat(properties, key);
    }
    
    private float getFloat(Properties config, String key) throws RuntimeException {
        String value = config.getProperty(key);
        if (null != value) {
            return Float.parseFloat(value);
        } else {
            throw new RuntimeException("Config value missing: " + key);
        }
    }
}
