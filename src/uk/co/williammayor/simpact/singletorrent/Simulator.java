package uk.co.williammayor.simpact.singletorrent;

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
        Network network = new Network(config.getN());
        network.getRandomNodes(1)[0].author(config.getR(), config.getAuthorAvailability());
        for (Node n : network.getAll()) {
            n.check();
        }
        for (Node n : network.getRandomNodes(config.getMaxPopularity())) {
            n.arriveAfter((int) arrivalDistribution.sample(), (int) availabilityDistribution.sample());
        }
        while (!(Statistics.getCurrentAwareness() == 0 || Statistics.getCurrentPopularity() == 0)) {
            Statistics.step();
            for (Node n : network.getAll()) {
                n.step(config);
            }
            for (Node n : network.getAll()) {
                n.check();
            }
        }
    }
}
