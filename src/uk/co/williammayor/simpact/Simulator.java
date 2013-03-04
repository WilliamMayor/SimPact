package uk.co.williammayor.simpact;

import org.apache.commons.math3.distribution.WeibullDistribution;

public class Simulator {
    
    private Config config;
    private WeibullDistribution availabilityDistribution;
    private WeibullDistribution arrivalDistribution;
    
    public Simulator(Config config) {
        this.config = config;
        availabilityDistribution = new WeibullDistribution(config.getFloat("availability_distribution_shape"), config.getFloat("availability_distribution_scale"));
        arrivalDistribution = new WeibullDistribution(config.getFloat("arrival_distribution_shape"), config.getFloat("arrival_distribution_scale"));
    }
    
    public void trial() {
        Network network = new Network(config.getInt("n"));
        Node[] activeNodes = network.getRandomNodes(config.getInt("max_popularity") + 1);
        activeNodes[0].author(config.getInt("r"), config.getInt("author_availability"));
        for (Node n : network.getAll()) {
            n.check();
        }
        int arriveAfter = 0;
        for (int i = 1; i < activeNodes.length; i++) {
            Node n = activeNodes[i];
            arriveAfter += (int) arrivalDistribution.sample();
            n.arriveAfter(arriveAfter);
            n.stayFor((int) availabilityDistribution.sample());
        }
        while (Statistics.getCurrentAwareness() != 0 && Statistics.getCurrentPopularity() != 0) {
            Statistics.step();
            for (Node n : activeNodes) {
                n.step(config);
            }
            for (Node n : network.getAll()) {
                n.check();
            }
        }
    }
}
