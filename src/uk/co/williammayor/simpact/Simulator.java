package uk.co.williammayor.simpact;

import java.util.Random;
import org.apache.commons.math3.distribution.WeibullDistribution;
import uk.co.williammayor.simpact.Config;
import uk.co.williammayor.simpact.Network;
import uk.co.williammayor.simpact.Statistics;

public class Simulator {
    
    private Config config;
    private WeibullDistribution availabilityDistribution;
    private WeibullDistribution arrivalDistribution;
    
    public Simulator(Config config) {
        this.config = config;
        availabilityDistribution = new WeibullDistribution(config.getAvailabilityDistributionShape(), config.getAvailabilityDistributionScale());
        arrivalDistribution = new WeibullDistribution(config.getArrivalDistributionShape(), config.getArrivalDistributionScale());
    }
    
    public void trial() {
        System.err.println("    Making network");
        Network network = new Network(config.getN());
        System.err.println("    Setting author");
        network.getRandomNodes(1)[0].author(config.getR(), config.getAuthorAvailability());
        System.err.println("    Initialising statistics");
        for (Node n : network.getAll()) {
            n.check();
        }
        System.err.println("    Setting arrivals and departures");
        int arriveAfter = 0;
        Node[] activeNodes = network.getRandomNodes(config.getMaxPopularity());
        for (Node n : activeNodes) {
            arriveAfter +=(int) arrivalDistribution.sample();
            n.arriveAfter(arriveAfter, (int) availabilityDistribution.sample());
        }
        System.err.println("    Entering loop");
        while (Statistics.getCurrentAwareness() != 0 && Statistics.getCurrentPopularity() != 0) {
            Statistics.step();
            System.err.println("        Increase time");
            for (Node n : activeNodes) {
                n.step(config);
            }
            System.err.println("        Update statistics");
            for (Node n : network.getAll()) {
                n.check();
            }
            System.err.println("             Awareness: " + Statistics.getCurrentAwareness());
            System.err.println("            Popularity: " + Statistics.getCurrentPopularity());
        }
    }
}
