package uk.co.williammayor.simpact;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class Main {

    public static void main(String[] args) throws FileNotFoundException, IOException, InterruptedException {
        Log results = new Log("results.txt");
        Log status = new Log("status.txt");
        
        String propertiesFilename = "simulation.properties";
        Properties properties = new Properties();
        FileInputStream in = new FileInputStream(propertiesFilename);
        properties.load(in);
        Config config = new Config(properties);
        
        Simulator simulator = new Simulator(config);
        int trialsRequired = config.getInt("trials", 1000);
        for (int i = 0; i < trialsRequired; i++) {
            trialsRequired = Math.max(trialsRequired, Statistics.requiredTrials());
            status.println("Require " + trialsRequired + " trials");
            status.println("Starting trial " + (i+1));
            
            simulator.trial();
            Statistics.reset();
            results.println(Statistics.summarise());
            
            results.reset();
            status.reset();
        }
    }
}
