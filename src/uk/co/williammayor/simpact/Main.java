package uk.co.williammayor.simpact;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class Main {

    public static void main(String[] args) throws FileNotFoundException, IOException {
        String propertiesFilename = "simulation.properties";
        Properties properties = new Properties();
        FileInputStream in = new FileInputStream(propertiesFilename);
        properties.load(in);
        Config config = new Config(properties);
        Simulator simulator = new Simulator(config);
        int trialsRequired = config.getTrials();
        for (int i = 0; i < trialsRequired; i++) {
            System.err.println("Starting trial " + (i+1));
            simulator.trial();
            Statistics.reset();
            trialsRequired = Math.max(trialsRequired, Statistics.requiredTrials());
            System.err.println("Require " + trialsRequired + " trials");
            System.err.println("Finished trial " + (i+1));
        }
        Statistics.print();
    }
}
