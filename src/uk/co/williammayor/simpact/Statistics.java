package uk.co.williammayor.simpact;

import java.util.ArrayList;
import java.util.HashMap;
import org.apache.commons.math3.distribution.NormalDistribution;

public class Statistics {

    private static final HashMap<String, Data> all = new HashMap<String, Data>();
    
    private static int time = 0;
       
    public static void reset() {
        bank();
        time = 0;
    }
    
    public static void step() {
        bank();
        time++;
    }
    
    private static void bank() {
        for (Data d : all.values()) {
            d.bank(time);
        }
    }
    
    public static void add(String key, double value) {
        if (!all.containsKey(key)) {
            all.put(key, new Data());
        }
        all.get(key).add(value);
    }
    
    public static void alter(String key, double value) {
        if (!all.containsKey(key)) {
            all.put(key, new Data());
        }
        all.get(key).alter(value);
    }
    
    public static int requiredTrials() {
        double a = 0.05;
        double e = 0.05;
        NormalDistribution n = new NormalDistribution();
        double z = n.inverseCumulativeProbability(1-a/2);
        int requiredTrials = 0;
        for (Data d : all.values()) {
            for (MovingAverage ma : d.all) {
                double mean = ma.getAverage();
                double standardDeviation = ma.getStandardDeviation();
                int trials = (int) Math.ceil((z*standardDeviation)/(e*mean));
                requiredTrials = Math.max(trials, requiredTrials);
            }
        }
        return requiredTrials;
    }
    
    public static String summarise() {
        StringBuilder sb = new StringBuilder("time");
        ArrayList<String> headers = new ArrayList<String>(all.keySet());
        for (String header : headers) {
            sb.append(", ").append(header);
        }
        for (int i = 0; i < all.get(headers.get(0)).all.size(); i++) {
            sb.append("\n").append(i);
            for (String header : headers) {
                ArrayList<MovingAverage> list = all.get(header).all;
                sb.append(", ");
                sb.append((double)Math.round(list.get(i).getAverage() * 100000) / 100000);
            }
        }
        return sb.toString();
    }
    
    private static class Data {
        private final ArrayList<MovingAverage> all;
        private MovingAverage current;
        
        public Data() {
            all = new ArrayList<MovingAverage>();
            current = new MovingAverage();
        }
        
        public void bank(int index) {
            while (all.size() < index + 1) {
                    all.add(new MovingAverage());
                }
            all.get(index).add(current);
            current = new MovingAverage();
        }
        
        public void add(double value) {
            current.add(value);
        }
        
        public void alter(double value) {
            if (null == current) {
                current = new MovingAverage();
                current.add(value);
            } else {
                current.alter(value);
            }
        }
    }

    private static class MovingAverage {

        private int count;
        private double total;
        private double squaredTotal;
        
        public double getAverage() {
            return total / count;
        }
        
        public double getStandardDeviation() {
            return Math.sqrt(squaredTotal / count - Math.pow(total / count, 2));
        }

        public void add(double data) {
            double squared = Math.pow(data, 2);
            if (squared > 0 && squaredTotal > Double.MAX_VALUE - squared) {
                throw new RuntimeException("squaredTotal overflow");
            }
            total += data;
            squaredTotal += squared;
            count++;
        }
        
        public void add(MovingAverage data) {
            if (data.squaredTotal > 0 && squaredTotal > Double.MAX_VALUE - data.squaredTotal) {
                throw new RuntimeException("squaredTotal overflow");
            }
            total += data.total;
            squaredTotal += data.squaredTotal;
            count += data.count;
        }
        
        public void alter(double data) {
            add(data);
            count = Math.max(count - 1, 1);
        }
    }
}
