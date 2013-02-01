package uk.co.williammayor.simpact.accuracy;

import java.util.HashMap;
import java.util.TreeSet;

public class Node implements Comparable<Node> {
   
    private final int id;
    private boolean done;
    private Torrent currentTorrent;
    private HashMap<Torrent,TreeSet<Node>> extendedIndex;
    private TreeSet<Node> passiveIndex;
    
    public Node(final int id) {
        this.id = id;
        this.done = false;
        this.extendedIndex = new HashMap<Torrent, TreeSet<Node>>();
    }

    public int getId() {return id;}
    public boolean isDone() {return done;}
    public Torrent getCurrentTorrent() {return currentTorrent;}
    public HashMap<Torrent,TreeSet<Node>> getExtendedIndex() {return extendedIndex;}
    public TreeSet<Node> getPassiveIndex() { return passiveIndex;}
       
    public void author(final Torrent torrent) {
        currentTorrent = torrent;
        passiveIndex = new TreeSet<Node>();
        passiveIndex.add(this);
        addToExtendedIndex(this, torrent);
        this.done = true;
    }

    public void bootstrap() {
        if (done) {
            Node[] nodes = Simulator.getInstance().getRandomNodes(Simulator.BOOTSTRAP_COUNT, id);
            for (Node n : nodes) {
                n.query(this, currentTorrent);
            }
        }
    }
    
    public void find() {
        if (done) {
            return;
        }
        if (null == currentTorrent) {
            currentTorrent = Simulator.getInstance().getRandomTorrent();
            passiveIndex = new TreeSet<Node>();
        }
        while (!done) {
            TreeSet<Node> results;
            if (extendedIndex.containsKey(currentTorrent)) {
                results = extendedIndex.get(currentTorrent);
            }
            else {
                results = new TreeSet<Node>();
                Node[] nodes = Simulator.getInstance().getRandomNodes(Simulator.Z, id);
                for (int i = 0; i < nodes.length; i++) {
                    Node n = nodes[i];
                    results.addAll(n.query(this, currentTorrent));         
                }
                results.remove(this);
            }
            for (Node n : results) {
                passiveIndex.addAll(n.peerExchange(this, currentTorrent));
            }
            done = !passiveIndex.isEmpty();
        }
    }
    
    public boolean step() {
        if (!done) {
            StringBuilder sb = new StringBuilder();
            sb.append("Node ")
              .append(id)
              .append(": Looking for ")
              .append(currentTorrent.getId())
              .append("\n    Querying: [");
            TreeSet<Node> results = new TreeSet<Node>();
            Node[] nodes = Simulator.getInstance().getRandomNodes(Simulator.Z, id);
            for (int i = 0; i < nodes.length; i++) {
                Node n = nodes[i];
                sb.append(n.id).append(" ");
                results.addAll(n.query(this, currentTorrent));         
            }
            results.remove(this);
            sb.append("]\n    Results: [");
            for (Node n : results) {
                sb.append(n.id).append(" ");
                passiveIndex.addAll(n.peerExchange(this, currentTorrent));
            }
            sb.append("]\n    Peers: [");
            for (Node n : passiveIndex) {
                sb.append(n.id).append(" ");
            }
            sb.append("]\n");
            done = !passiveIndex.isEmpty();
            System.out.println(sb.toString());
        }
        return done;
    }
    
    public TreeSet<Node> query(Node n, Torrent t) {
        addToExtendedIndex(n, t);
        return extendedIndex.get(t);
    }
    
    public TreeSet<Node> peerExchange(Node n, Torrent t) {
        if (t.equals(currentTorrent) && done) {
            passiveIndex.add(n);
            return passiveIndex;
        }
        return new TreeSet<Node>();
    }
    
    public boolean aware(Torrent t) {
        return extendedIndex.containsKey(t) || currentTorrent == t;
    }
    
    private void addToExtendedIndex(Node n, Torrent t) {
        if (!extendedIndex.containsKey(t)) {
            extendedIndex.put(t, new TreeSet<Node>());
        }
        extendedIndex.get(t).add(n);
    }

    @Override
    public int compareTo(Node n) {
        int diff = n.id - this.id;
        return (diff == 0) ? 0 : (diff > 0) ? 1 : -1;
    }
}
