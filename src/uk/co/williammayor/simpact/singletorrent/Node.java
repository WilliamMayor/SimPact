package uk.co.williammayor.simpact.singletorrent;

import java.util.HashSet;
import uk.co.williammayor.simpact.Config;
import uk.co.williammayor.simpact.Network;
import uk.co.williammayor.simpact.Statistics;

public class Node {
    
    public static enum State {PASSIVE, ACTIVE, INACTIVE}
    public static int Z;
   
    private int id;
    private int networkPosition;
    private final Network network;
    private HashSet<Node> index;
    private HashSet<Node> peers;
    private int availability;
    private int timeToSearch;
    private State state;
            
    public Node(final Network network, final int id, final int networkPosition) {
        this.id = id;
        this.networkPosition = networkPosition;
        this.network = network;
        state = State.PASSIVE;
    }

    public int getId() {
        return id;
    }
    
    public int getPosition() {
        return networkPosition;
    }
    
    public State getState() {
        return state;
    }
    /**
     * Tell the node to become the author of the torrent.
     * The author will create a HashSet<Node> for its index and peers list.
     * It will then make 'dummy' requests to config.getR() other nodes.
     */   
    public void author(int r, int availability) {
        index = new HashSet<Node>();
        peers = new HashSet<Node>();
        index.add(this);
        peers.add(this);
        this.availability = availability;
        state = State.ACTIVE;
        for (Node n : network.getRandomNodes(r)) {
            n.respond(this);
        }
    }
    /**
     * Go through the list of possible active nodes performing a peer exchange with each.
     * Returns the active-peer list, if found.
     * @param possibles Nodes that might be active (i.e. returned from a search)
     * @return The active-peer list if found, null if not.
     */
    private HashSet<Node> findPeers(HashSet<Node> possibles) {
        for (Node n : possibles) {
            HashSet<Node> exchanged = n.getPeers();
            if (null != exchanged) {
                return exchanged;
            }
        }
        return null;
    }
    
    public void search(int z) {
        if (null == index) {
            // This node has never been contacted before
            index = new HashSet<Node>();
        }
        else if (!index.isEmpty()) {
            // This node has been contacted and might be aware of an active node already
            peers = findPeers(index);
        }
        while (null == peers) {
            peers = findPeers(query(z));
        }
        peers.add(this);
        index.add(this);
        state = State.ACTIVE;
    }
    
    public HashSet<Node> query(int z) {
        HashSet<Node> results = new HashSet<Node>();
        for (Node n : network.getRandomNodes(z)) {
            results.addAll(n.respond(this));
        }
        return results;
    }
        
    public HashSet<Node> respond(Node from) {
        if (State.INACTIVE == state) {
            throw new RuntimeException("Trying to get a response from an inactive node.");
        }
        if (null == index) {
            index = new HashSet<Node>();
        }
        index.add(from);
        return index;
    }
    
    public HashSet<Node> getPeers() {
        return peers;
    }
    
    public void leave() {
        peers.remove(this);
        peers = null;
        index = null;
        state = State.INACTIVE;
        network.remove(this);
    }
    
    public void step(Config config) {
        if (state == State.ACTIVE) {
            if (--availability == 0) {
                leave();
            }
        }
        else if (state == State.PASSIVE) {
            if (timeToSearch-- <= 0) {
                search(config.getZ());
            }
        }
    }
    
    public void check() {
        if (null != index) {
            int badCount = 0;
            for (Node n : index) {
                if (n.getState() == State.INACTIVE) {
                    badCount++;
                }
            }
            Statistics.changeBadData(badCount);
            if (badCount != index.size()) {
                Statistics.changeAwareness(1);
            }
        }
        if (null != peers) {
            Statistics.changePopularity(1);
        }
    }
    
    public void arriveAfter(int timeToSearch, int availability) {
        this.timeToSearch = timeToSearch;
        this.availability = availability;
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
