package uk.co.williammayor.simpact;

import java.util.Arrays;
import java.util.HashSet;

public class Simulator {
    
    private Config config;
    
    public Simulator(Config config) {
        this.config = config;
    }
    
    public void trial() {
        Network network = new Network(config.getInt("n"));
        Node.NETWORK = network;
        Node[] activeNodes = network.getRandomNodes(config.getInt("constant_popularity"));
        Node author = activeNodes[0];
        activeNodes = Arrays.copyOfRange(activeNodes, 1, activeNodes.length);
        author.author();
        for (Node n : network.getAll()) {
            n.check();
        }
        for (Node n : activeNodes) {
            n.activate();
        }
        int time = 0;
        while (config.getInt("max_hours") > time) {
            time++;
            Statistics.step();
            Statistics.alter("left", 0);
            for (Node n : network.getAll()) {
                n.step();
            }
            for (Node n : network.getAll()) {
                n.check();
            }
        }
    }
}
