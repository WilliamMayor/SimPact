package uk.co.williammayor.simpact.singletorrent;

import java.util.HashSet;
import uk.co.williammayor.simpact.Config;
import uk.co.williammayor.simpact.Network;
import uk.co.williammayor.simpact.Statistics;

public class Node {
    
    public static enum State {PRE, AUTHORING, DOWNLOADING, SEEDING, POST}
   
    private int id;
    private final Network network;
    private HashSet<Node> index;
    private HashSet<Node> peers;
    private int downloadTime;
    private int badDataCount;
    private State state;
    
    private HashSet<Node> indexingMe;
        
    public Node(final Network network, final int id) {
        this.id = id;
        this.network = network;
        state = State.PRE;
    }

    public int getId() {
        return id;
    }
    
    public State getState() {
        return state;
    }
       
    public void author(Config config, Statistics stats) {
        indexingMe = new HashSet<Node>();
        index = new HashSet<Node>();
        peers = new HashSet<Node>();
        
        index.add(this);
        indexingMe.add(this);
        peers.add(this);
        
        Node[] nodes = network.getRandomNodes(config.getR());
        for (Node n : nodes) {
            n.request(this, stats);
            indexingMe.add(n);
        }
        
        stats.changePopularity(1);
        stats.changeAwareness(1);
        
        downloadTime = config.getAuthorAvailability();
        state = State.AUTHORING;
    }
    
    /**
     * Check to see if any node in the collection is a torrent owner.
     * If at least one is, this node's peers list is set.
     * This method does not add this node to the peers list
     * @param possibles The nodes that might possibly own the torrent
     */
    private void possiblePeers(HashSet<Node> possibles) {
        HashSet<Node> thePeers = null;
        for (Node n : possibles) {
            if (n.equals(this)) {
                continue;
            }
            HashSet<Node> exchanged = n.peerExchange(this);
            if (null != exchanged) {
                thePeers = exchanged;
                index.add(n);
                indexingMe.add(n);
            }
        }
        peers = thePeers;
    }
    
    public void search(Config config, Statistics stats) {
        if (null == indexingMe) {
            indexingMe = new HashSet<Node>();
        }
        if (null == index) {
            index = new HashSet<Node>();
            stats.changeAwareness(1);
        }
        if (null != index && !index.isEmpty()) {
            possiblePeers(index);
        }
        boolean success = (null != peers && !peers.isEmpty());
        while (!success) {
            success = query(config.getZ(), stats);
        }
        peers.add(this);
        index.add(this);
        indexingMe.add(this);
        stats.changePopularity(1);
        downloadTime = config.getDownloadTime();
        state = State.DOWNLOADING;
    }
    
    public boolean query(int z, Statistics stats) {
        HashSet<Node> results = new HashSet<Node>();
        for (Node n : network.getRandomNodes(z)) {
            results.addAll(n.request(this, stats));
            indexingMe.add(n);
        }
        possiblePeers(results);
        return (null != peers);
    }
        
    public HashSet<Node> request(Node from, Statistics stats) {
        if (null == index) {
            index = new HashSet<Node>();
        }
        if (index.isEmpty()) {
            stats.changeAwareness(1);
        }
        index.add(from);
        return index;
    }
    
    public HashSet<Node> peerExchange(Node node) {
        if (null != peers) {
            index.add(node);
            indexingMe.add(node);
        }
        return peers;
    }
    
    private void removeMe(Node node, Statistics stats) {
        index.remove(node);
        if (null != indexingMe) {
            indexingMe.remove(node);
        }
        stats.changeBadData(1);
        badDataCount++;
        if (index.isEmpty()) {
            stats.changeAwareness(-1);
        }
    }
    
    public void leave(Statistics stats) {
        peers.remove(this);
        index.remove(this);
        indexingMe.remove(this);
        for (Node n : indexingMe) {
            n.removeMe(this, stats);
        }
        stats.changePopularity(-1);
        stats.changeAwareness(-1);
        stats.changeBadData(-badDataCount);
        peers = null;
        index = null;
        badDataCount = 0;
        state = State.POST;
    }
    
    public void download(Statistics stats) {
        if (--downloadTime == 0) {
            if (state == State.AUTHORING) {
                leave(stats);
            }
            else {
                state = State.SEEDING;
            }
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
