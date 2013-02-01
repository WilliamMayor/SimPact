package uk.co.williammayor.simpact.awareness;

public class Node {
   
    private final int id;
    private int aware;
    private Awareness simulator;
        
    public Node(final Awareness simulator, final int id) {
        this.simulator = simulator;
        this.id = id;
        this.aware = -1;
    }

    public int getId() {return id;}
    public boolean isAware() {return (aware != -1);}
       
    public void author() {
        aware = id;
        simulator.incrementAware();
    }

    public void bootstrap(int r) {
        Node[] nodes = simulator.getRandomNodes(r, id);
        for (Node n : nodes) {
            n.query(id);
        }
    }
    
    public void find(int z) {
        while (aware == -1) {
            Node[] nodes = simulator.getRandomNodes(z, id);
            for (Node n : nodes) {
                int follow = n.query(id);
                if (follow != id) {
                    aware = follow;
                }
            }
        }
    }
        
    public int query(int queryNode) {
        if (aware == -1) {
            aware = queryNode;
            simulator.incrementAware();
        }
        return aware;
    }
}
