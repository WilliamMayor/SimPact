package uk.co.williammayor.simpact;

import java.util.ArrayList;
import org.apache.commons.math3.distribution.NormalDistribution;

public class Statistics {

    private static final ArrayList<MovingAverage> popularity = new ArrayList<MovingAverage>();
    private static final ArrayList<MovingAverage> awareness = new ArrayList<MovingAverage>();
    private static final ArrayList<MovingAverage> badData = new ArrayList<MovingAverage>();
    private static final ArrayList<MovingAverage> joined = new ArrayList<MovingAverage>();
    private static final ArrayList<MovingAverage> left = new ArrayList<MovingAverage>();
    
    private static int time;
    private static int currentPopularity;
    private static int currentAwareness;
    private static int currentBadData;
    private static int currentJoined;
    private static int currentLeft;
   
    public static int getCurrentPopularity() {
        return currentPopularity;
    }

    public static int getCurrentAwareness() {
        return currentAwareness;
    }
    
    public static int getTime() {
        return time;
    }
    
    public static void reset() {
        bank();
        currentPopularity = 0;
        currentAwareness = 0;
        currentBadData = 0;
        currentJoined = 0;
        currentLeft = 0;
        time = 0;
    }
    
    public static void step() {
        bank();
        currentPopularity = 0;
        currentAwareness = 0;
        currentBadData = 0;
        currentJoined = 0;
        currentLeft = 0;
        time++;
    }
    
    private static void bank() {
        bank(popularity, time, currentPopularity);
        bank(awareness, time, currentAwareness);
        bank(badData, time, currentBadData);
        bank(joined, time, currentJoined);
        bank(left, time, currentLeft);
    }
    
    private static void bank(ArrayList<MovingAverage> list, int time, int value) {
        while (list.size() < time + 1) {
            list.add(new MovingAverage());
        }
        list.get(time).add(value);
    }

    public static void changePopularity(int value) {
        currentPopularity += value;
    }
    
    public static void changeAwareness(int value) {
        currentAwareness += value;
    }
    
    public static void changeBadData(int value) {
        currentBadData += value;
    }
    
    public static void changeJoined(int value) {
        currentJoined += value;
    }
    
    public static void changeLeft(int value) {
        currentLeft += value;
    }
    
    public static int requiredTrials() {
        double a = 0.05;
        double e = 0.05;
        NormalDistribution n = new NormalDistribution();
        double z = n.inverseCumulativeProbability(1-a/2);
        int requiredTrials = 0;
        for (ArrayList<MovingAverage> list : new ArrayList[]{popularity, awareness, badData, joined, left}) {
            for (MovingAverage ma : list) {
                double mean = ma.getAverage();
                double standardDeviation = ma.getStandardDeviation();
                int trials = (int) Math.ceil((z*standardDeviation)/(e*mean));
                requiredTrials = Math.max(trials, requiredTrials);
            }
        }
        return requiredTrials;
    }
    
    public static void print() {
        StringBuilder sb = new StringBuilder("time, popularity, awareness, bad_data, joined, left");
        for (int i = 0; i < popularity.size(); i++) {
            sb.append("\n").append(i);
            for (ArrayList<MovingAverage> list : new ArrayList[]{popularity, awareness, badData, joined, left}) {
                sb.append(", ");
                if (list.size() > i) {
                    //sb.append(String.format("%.5g%n", list.get(i).getAverage()));
                    sb.append((double)Math.round(list.get(i).getAverage() * 100000) / 100000);
                }
            }
        }
        System.out.println(sb.toString());
    }

    public static class MovingAverage {

        private int count;
        private double total;
        private double squaredTotal;
        
        public double getAverage() {
            return total / count;
        }
        
        public double getStandardDeviation() {
            return Math.sqrt(squaredTotal / count - Math.pow(total / count, 2));
        }

        public void add(int data) {
            if (data > 0 && squaredTotal > Double.MAX_VALUE - data) {
                throw new RuntimeException("squaredTotal overflow");
            }
            total += data;
            squaredTotal += Math.pow(data, 2);
            count++;
        }
    }
}
