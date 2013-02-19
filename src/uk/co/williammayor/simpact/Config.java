package uk.co.williammayor.simpact;

import java.util.Properties;

public class Config {
    
    private final int m;
    private final int trials;
    private final int n;
    private final int r;
    private final int maxPopularity;
    private final int z;
    private final float availabilityDistributionShape;
    private final float availabilityDistributionScale;
    private final float arrivalDistributionShape;
    private final float arrivalDistributionScale;
    private final int authorAvailability;
    private final int maxTime;
    
    
    public Config(Properties properties) {
        m = getInt(properties, "m", 0);
        trials = getInt(properties, "trials", 0);
        n = getInt(properties, "n", 0);
        r = getInt(properties, "r", 0);
        maxPopularity = getInt(properties, "max_popularity", 0);
        z = getInt(properties, "z", 0);
        availabilityDistributionShape = getFloat(properties, "availability_distribution_shape", 0);
        availabilityDistributionScale = getFloat(properties, "availability_distribution_scale", 0);
        arrivalDistributionShape = getFloat(properties, "arrival_distribution_shape", 0);
        arrivalDistributionScale = getFloat(properties, "arrival_distribution_scale", 0);
        authorAvailability = getInt(properties, "author_availability", 1);
        maxTime = getInt(properties, "max_time", 20);
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

    public float getArrivalDistributionShape() {
        return arrivalDistributionShape;
    }

    public float getArrivalDistributionScale() {
        return arrivalDistributionScale;
    }

    public float getAvailabilityDistributionShape() {
        return availabilityDistributionShape;
    }

    public float getAvailabilityDistributionScale() {
        return availabilityDistributionScale;
    }
    
    public int getAuthorAvailability() {
        return authorAvailability;
    }
    
    public int getMaxTime() {
        return maxTime;
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
