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
        Statistics stats = new Statistics();
        Simulator simulator = new Simulator();
        for (int i = 0; i < config.getTrials(); i++) {
            System.err.println("Trial " + i);
            simulator.run(config, stats);
            stats.reset();
        }
        stats.print();
    }
}
