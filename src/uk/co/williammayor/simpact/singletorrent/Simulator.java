package uk.co.williammayor.simpact.singletorrent;

import java.util.Random;
import org.apache.commons.math3.distribution.WeibullDistribution;
import uk.co.williammayor.simpact.Config;
import uk.co.williammayor.simpact.Network;
import uk.co.williammayor.simpact.Statistics;

public class Simulator {
    
    private Config config;
    private Random random;
    private WeibullDistribution wb;
    
    public Simulator(Config config) {
        this.config = config;
        random = new Random();
        wb = new WeibullDistribution(config.getAvailabilityDistributionShape(), config.getAvailabilityDistributionScale());
    }
    
    public void trial() {
        Network network = new Network(config.getN());
        network.getRandomNodes(1)[0].author(config.getR(), config.getAuthorAvailability());
        for (Node n : network.getAll()) {
            n.check();
        }
        int popularity = 1;
        while (!(Statistics.getCurrentAwareness() == 0 || Statistics.getCurrentPopularity() == 0)) {
            Statistics.step();
            for (Node n : network.getAll()) {
                n.step();
            }
            int searched = 0;
            while (searched < config.getSearchesPerHour() && popularity < config.getMaxPopularity()) {
                Node n = network.getRandomNodes(1)[0];
                if (n.getState() == Node.State.PASSIVE) {
                    n.search(config.getZ(), (int) wb.sample());
                    searched++;
                    popularity++;
                }
            }
            for (Node n : network.getAll()) {
                n.check();
            }
        }
    }
}
