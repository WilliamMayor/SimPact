package uk.co.williammayor.simpact;

import java.util.HashSet;
import org.apache.commons.math3.distribution.ExponentialDistribution;

public class Node {
    
    public static enum State {PASSIVE, WAITING, ACTIVE, DEAD}
    public static Network NETWORK;
    public static Config CONFIG;
    public static ExponentialDistribution ABORT_DISTRIBUTION;
    public static ExponentialDistribution SEED_FOR_DISTRIBUTION;
    
    public static void setup(Config config) {
        CONFIG = config;
        ABORT_DISTRIBUTION = new ExponentialDistribution(config.getFloat("average_abort_time"));
        SEED_FOR_DISTRIBUTION = new ExponentialDistribution(config.getFloat("average_seed_time"));
    }
   
    private int id;
    private int networkPosition;
    private HashSet<Node> index;
    private HashSet<Node> peers;
    private int resetAfter;
    private State state;
            
    public Node(final int id, final int networkPosition) {
        this.id = id;
        this.networkPosition = networkPosition;
        state = State.PASSIVE;
    }
    
    public int getPosition() {
        return networkPosition;
    }
    
    public HashSet<Node> getPeers() {
        return peers;
    }

    /**
     * Tell the node to become the author of the torrent.
     * The author will create a HashSet<Node> for its index and peers list.
     * It will then make 'dummy' requests to config.getR() other nodes.
     */   
    public void author() {
        index = new HashSet<Node>();
        peers = new HashSet<Node>();
        index.add(this);
        peers.add(this);
        state = State.ACTIVE;
        for (Node n : NETWORK.getRandomNodes(CONFIG.getInt("r"))) {
            n.respond(this);
        }
    }
    /**
     * Tell the node that it should attempt a download at some point
     */
    public void activate() {
        state = State.WAITING;
    }
    /**
     * Tell the node it is to remain passive for an amount of time.
     * The amount of time before the reset check is determined by the estimated
     * download speed, abort chance and seed time.
     */
    public void passive() {
        resetAfter = Math.min(100 / CONFIG.getInt("download_bandwidth"), (int) ABORT_DISTRIBUTION.sample()) + (int) SEED_FOR_DISTRIBUTION.sample();
        state = State.PASSIVE;
    }
    /**
     * Go through the list of possible active nodes performing a peer exchange with each.
     * Returns the active-peer list, if found.
     * @param possibles Nodes that might be active (i.e. returned from a search)
     * @return The active-peer list if found, null if not.
     */
    private HashSet<Node> findPeers(HashSet<Node> possibles) {
        for (Node n : possibles) {
            if (null != n.getPeers()) {
                return n.getPeers();
            }
        }
        return null;
    }
    /**
     * Search for the torrent's tracking data by repeatedly querying for it
     */
    public void search() {
        if (null == index) {
            // This node has never been contacted before
            index = new HashSet<Node>();
        }
        else if (!index.isEmpty()) {
            // This node has been contacted and might be aware of an active node already
            peers = findPeers(index);
        }
        int z = CONFIG.getInt("z");
        int requestCount = 0;
        while (null == peers) {
            peers = findPeers(query(z));
            requestCount += z;
        }
        peers.add(this);
        index.add(this);
        state = State.ACTIVE;
        Statistics.alter("joined", 1);
        Statistics.add("request_count", requestCount);
    }
    /**
     * Make a request to z randomly selected nodes 
     * @param z
     * @return The combined results obtained from all z nodes, duplicates removed
     */
    private HashSet<Node> query(int z) {
        HashSet<Node> results = new HashSet<Node>();
        for (Node n : NETWORK.getRandomNodes(z)) {
            results.addAll(n.respond(this));
        }
        Statistics.add("response_size", results.size() - 1); // -1 to remove self reference
        return results;
    }
    /**
     * Provide the requesting node with any details on nodes that are interested in the torrent
     * @param from The requesting node
     * @return This node's index
     */    
    public HashSet<Node> respond(Node from) {
        if (null == index) {
            index = new HashSet<Node>();
        }
        index.add(from);
        return index;
    }
    
    public void step() {
        switch (state) {
            case PASSIVE:
                resetAfter--;
                if (resetAfter <= 0) {
                    if (Math.random() <= CONFIG.getFloat("recycle_chance")) {
                        passive();
                    } else {
                        NETWORK.replace(this);
                        state = State.DEAD;
                    }
                }
                break;
            case WAITING:
                search();
                break;
        }
    }
    
    public void check() {
        if (null != index) {
            int badCount = 0;
            for (Node n : index) {
                if (n.state == State.DEAD) {
                    badCount++;
                }
            }
            Statistics.alter("bad_data", badCount);
            if (badCount != index.size()) {
                Statistics.alter("awareness", 1);
            }
        }
        if (state == State.ACTIVE) {
            Statistics.alter("seeders", 1);
            Statistics.alter("leechers", 0);
        }
    }
    
    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Node other = (Node) obj;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }
}
