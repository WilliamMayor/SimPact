package uk.co.williammayor.simpact.singletorrent;

import java.util.Random;
import uk.co.williammayor.simpact.Config;
import uk.co.williammayor.simpact.Network;
import uk.co.williammayor.simpact.Statistics;

public class Simulator {
    
    private Random random;
    
    public Simulator() {
        random = new Random();
    }
    
    public void run(Config config, Statistics stats) {
        Network network = new Network(config.getN());
        network.getRandomNodes(1)[0].author(config, stats);
        int popularity = 1;
        while (!(stats.getCurrentAwareness() == 0 || stats.getCurrentPopularity() == 0)) {
            stats.step();
            for (Node n : network.getAll()) {
                switch (n.getState()) {
                    case AUTHORING:
                        n.download(stats);
                        break;
                    case DOWNLOADING:
                        n.download(stats);
                        if (random.nextFloat() <= config.getAbandonProbability()) {
                            n.leave(stats);
                        }
                        break;
                    case SEEDING:
                        if (random.nextFloat() > config.getSeedProbability()) {
                            n.leave(stats);
                        }
                        break;
                }
            }
            int searched = 0;
            while (searched < config.getSearchesPerHour() && popularity < config.getMaxPopularity()) {
                Node n = network.getRandomNodes(1)[0];
                if (n.getState() == Node.State.PRE) {
                    n.search(config, stats);
                    searched++;
                    popularity++;
                }
            }
        }
    }
}
