package uk.co.williammayor.simpact;

import java.util.Properties;

public class Config {
    
    private final int m;
    private final int trials;
    private final int n;
    private final int r;
    private final int maxPopularity;
    private final int z;
    private final int downloadTime;
    private final int authorAvailability;
    private final int searchesPerHour;
    private final float abandonProbability;
    private final float seedProbability;
    
    
    public Config(Properties properties) {
        m = getInt(properties, "m", 0);
        trials = getInt(properties, "trials", 0);
        n = getInt(properties, "n", 0);
        r = getInt(properties, "r", 0);
        maxPopularity = getInt(properties, "max_popularity", 0);
        z = getInt(properties, "z", 0);
        downloadTime = getInt(properties, "download_time", 0);
        searchesPerHour = getInt(properties, "searches_per_hour", 1);
        authorAvailability = getInt(properties, "author_availability", 1);
        abandonProbability = getFloat(properties, "abandon_probability", 1f);
        seedProbability = getFloat(properties, "seed_probability", 1f);
    }

    public int getM() {
        return m;
    }

    public int getTrials() {
        return trials;
    }
    
    public int getN() {
        return n;
    }

    public int getR() {
        return r;
    }

    public int getMaxPopularity() {
        return maxPopularity;
    }

    public int getZ() {
        return z;
    }

    public int getSearchesPerHour() {
        return searchesPerHour;
    }

    public int getDownloadTime() {
        return downloadTime;
    }

    public float getAbandonProbability() {
        return abandonProbability;
    }

    public float getSeedProbability() {
        return seedProbability;
    }
    
    public int getAuthorAvailability() {
        return authorAvailability;
    }
    
    private int getInt(Properties config, String key, int _default) {
        String value = config.getProperty(key);
        if (null != value) {
            return Integer.parseInt(value);
        } else {
            return _default;
        }
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
