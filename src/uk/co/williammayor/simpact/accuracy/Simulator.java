package uk.co.williammayor.simpact.accuracy;

import java.util.Random;

public class Simulator {

    private Simulator() {
        this.random = new Random();
    }

    private static class SimulatorHolder { 
        public static final Simulator INSTANCE = new Simulator();
    }

    public static Simulator getInstance() {
        return SimulatorHolder.INSTANCE;
    }
    
    public static final int NODE_COUNT = 10000;
    public static final int TORRENT_COUNT = 1;
    public static final int Z = (1000*NODE_COUNT/5400000)+1;
    public static final int BOOTSTRAP_COUNT = (5000*NODE_COUNT/5400000)+1;
    
    private Random random;
    
    private Node[] nodes;
    private Torrent[] torrents;

    public Node[] getRandomNodes(int count, int notMe) {
        int[] indicies = indicies(count, notMe);
        Node[] randomNodes = new Node[count];
        for (int i = 0; i < count; i++) {
            randomNodes[i] = nodes[indicies[i]];
        }
        return randomNodes;
    }
    
    public Torrent getRandomTorrent() {
        return torrents[random.nextInt(TORRENT_COUNT)];
    }

    private boolean contains(int[] values, int value, int currentIndex) {
        for (int i = 0; i < currentIndex; i++) {
            if (values[i] == value) {
                return true;
            }
        }
        return false;
    }

    private int[] indicies(int count) {
        return indicies(count, -1);
    }

    private int[] indicies(int count, int notThis) {
        int[] indicies = new int[count];
        for (int i = 0; i < count; i++) {
            int next = 0;
            do {
                next = random.nextInt(NODE_COUNT);
            } while (contains(indicies, next, i) || next == notThis);
            indicies[i] = next;
        }
        return indicies;
    }

    private void init() {
        nodes = new Node[NODE_COUNT];
        for (int i=0; i < NODE_COUNT; i++) {
            nodes[i] = new Node(i);
        }
        
        torrents = new Torrent[TORRENT_COUNT];
        for (int i=0; i < TORRENT_COUNT; i++) {
            torrents[i] = new Torrent(i);
        }
        //GEXF.getInstance().tick();
    }
    
    private void bootstrap() {
        int[] indicies = indicies(TORRENT_COUNT);
        for (int i = 0; i < TORRENT_COUNT; i++) {
            Node n = nodes[indicies[i]];
            n.author(torrents[i]);
            n.bootstrap();
            //GEXF.getInstance().tick();
        }
    }
    
    private int awareCount() {
        int count = 0;
        for (Node n : nodes) {
            if (n.aware(torrents[0])) {
                count++;
            }
        }
        return count;
    }
    
    private int[] simulate() {
        int[] sx = new int[NODE_COUNT];
        sx[0] = awareCount();
        int ownCount = 1;
        for (int i = 0; i < NODE_COUNT; i++) {
            Node n = nodes[i];
            if (!n.isDone()) {
                n.find();
                sx[ownCount] = awareCount();
                ownCount++;
            }
        }
        return sx;
    }

    public static void main(String[] args) {
        Simulator s = Simulator.getInstance();
        s.init();
        s.bootstrap();
        int[] sx = s.simulate();
        for (int i = 0; i< sx.length; i++) {
            System.out.println((i+1) + " " + sx[i]);
        }
    }
}
