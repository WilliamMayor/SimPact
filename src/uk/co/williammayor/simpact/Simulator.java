package uk.co.williammayor.simpact;

public class Simulator {
    
    private Config config;
    
    public Simulator(Config config) {
        this.config = config;
    }
    
    public void trial() {
        Network network = new Network(config.getInt("n"));
        Node[] activeNodes = network.getRandomNodes(config.getInt("max_popularity") + 1);
        activeNodes[0].author(config.getInt("r"), config.getInt("author_availability"));
        for (Node n : network.getAll()) {
            n.check();
        }
        for (int i = 1; i < activeNodes.length; i++) {
            Node n = activeNodes[i];
            n.arriveAfter(0);
            n.stayFor(1);
        }
        for (int i = 0; i < config.getInt("max_time"); i++) {
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
