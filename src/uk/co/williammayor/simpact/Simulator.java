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
        Node[] activeNodes = network.getRandomNodes(config.getInt("total_downloads") + 1);
        Node author = activeNodes[0];
        activeNodes = Arrays.copyOfRange(activeNodes, 1, activeNodes.length);
        author.author();
        boolean authorActive = true;
        for (Node n : network.getAll()) {
            n.check();
        }
        for (Node n : activeNodes) {
            n.activate();
        }
        HashSet<Node> peers = author.getPeers();
        while (!peers.isEmpty()) {
            Statistics.step();
            if (authorActive && peers.size() > 1) {
                author.leave();
                authorActive = false;
            }
            for (Node n : network.getAll()) {
                if (peers.isEmpty()) {
                    break;
                }
                n.step();
            }
            for (Node n : network.getAll()) {
                n.check();
            }
        }
    }
}
