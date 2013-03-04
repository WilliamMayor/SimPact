package uk.co.williammayor.simpact;

import java.util.Properties;

public class Config {
    
    private Properties properties;
    
    public Config(Properties properties) {
        this.properties = properties;
    }
    
    public int getInt(String key) {
        return getInt(key, 0);
    }
    
    public int getInt(String key, int _default) {
        return getInt(properties, key, _default);
    }

    private int getInt(Properties config, String key, int _default) {
        String value = config.getProperty(key);
        if (null != value) {
            return Integer.parseInt(value);
        } else {
            return _default;
        }
    }
    
    public float getFloat(String key) {
        return getFloat(key, 0);
    }
    
    public float getFloat(String key, float _default) {
        return getFloat(properties, key, _default);
    }
    
    private float getFloat(Properties config, String key, float _default) {
        String value = config.getProperty(key);
        if (null != value) {
            return Float.parseFloat(value);
        } else {
            return _default;
        }
    }
}
