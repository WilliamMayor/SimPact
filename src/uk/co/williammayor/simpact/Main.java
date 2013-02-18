package uk.co.williammayor.simpact;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import uk.co.williammayor.simpact.singletorrent.Simulator;

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
            simulator.trial();
            Statistics.reset();
            int newTrialsRequired = Statistics.requiredTrials();
            if (newTrialsRequired != 0) {
                trialsRequired = newTrialsRequired;
            }
            System.err.println("Trial " + (i+1) + " of " + trialsRequired + " required");
        }
        Statistics.print();
    }
}
