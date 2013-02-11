package uk.co.williammayor.simpact;

import java.util.ArrayList;

public class Statistics {

    private final ArrayList<MovingAverage> popularity;
    private final ArrayList<MovingAverage> awareness;
    private final ArrayList<MovingAverage> badData;
    
    private int time;
    private int currentPopularity;
    private int currentAwareness;
    private int currentBadData;
    
    
    public Statistics() {
        popularity = new ArrayList<MovingAverage>();
        awareness = new ArrayList<MovingAverage>();
        badData = new ArrayList<MovingAverage>();
        
        time = 0;
        currentPopularity = 0;
        currentAwareness = 0;
        currentBadData = 0;
    }

    public int getCurrentPopularity() {
        return currentPopularity;
    }

    public int getCurrentAwareness() {
        return currentAwareness;
    }
    
    public int getTime() {
        return time;
    }
    
    public void reset() {
        bank();
        currentPopularity = 0;
        currentAwareness = 0;
        currentBadData = 0;
        time = 0;
    }
    
    public void step() {
        bank();
        time++;
    }
    
    private void bank() {
        bank(popularity, time, currentPopularity);
        bank(awareness, time, currentAwareness);
        bank(badData, time, currentBadData);
    }
    
    private void bank(ArrayList<MovingAverage> list, int time, int value) {
        while (list.size() < time + 1) {
            list.add(new MovingAverage());
        }
        list.get(time).add(value);
    }

    public void changePopularity(int value) {
        currentPopularity += value;
    }
    
    public void changeAwareness(int value) {
        currentAwareness += value;
    }
    
    public void changeBadData(int value) {
        currentBadData += value;
    }
    
    public void print() {
        StringBuilder sb = new StringBuilder("time, popularity, awareness, bad_data");
        int maxLength = (awareness.size() > popularity.size()) ? awareness.size() : popularity.size();
        maxLength = (badData.size() > maxLength) ? badData.size() : maxLength;
        for (int i = 0; i < maxLength; i++) {
            sb.append("\n").append(i);
            for (ArrayList<MovingAverage> list : new ArrayList[]{popularity, awareness, badData}) {
                sb.append(", ");
                if (list.size() > i) {
                    //sb.append(String.format("%.5g%n", list.get(i).getAverage()));
                    sb.append((double)Math.round(list.get(i).getAverage() * 100000) / 100000);
                }
            }
        }
        System.out.println(sb.toString());
    }

    private class MovingAverage {

        private int count;
        private double average;

        public MovingAverage() {
            count = 0;
            average = 0d;
        }

        public double getAverage() {
            return average;
        }

        public void add(int data) {
            average = (data + count * average) / ++count;
        }
    }
}
