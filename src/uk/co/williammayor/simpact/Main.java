package uk.co.williammayor.simpact;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class Main {

    public static void main(String[] args) throws FileNotFoundException, IOException, InterruptedException {
        Log results = new Log("results");
        Log status = new Log("status");
        status.println("first entry test");
        
        String propertiesFilename = "simulation.properties";
        Properties properties = new Properties();
        FileInputStream in = new FileInputStream(propertiesFilename);
        properties.load(in);
        Config config = new Config(properties);
        Node.setup(config);
        
        Simulator simulator = new Simulator(config);
        int trialsRequired = config.getInt("trials");
        for (int i = 0; i < trialsRequired; i++) {
            trialsRequired = Math.max(trialsRequired, Statistics.requiredTrials());
            status.println("Require " + trialsRequired + " trials");
            status.println("Starting trial " + (i+1));
            System.out.println("Starting trial " + (i+1) + " of " + trialsRequired);
            
            simulator.trial();
            Statistics.reset();
            results.println(Statistics.summarise());
            
            results.reset();
            status.reset();
        }
    }
}
