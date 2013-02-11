package uk.co.williammayor.simpact;

import java.util.HashSet;
import java.util.Random;
import uk.co.williammayor.simpact.singletorrent.Node;

public class Network {
    
    private Random random;
    private int size;
    private int lastId;
    private Node[] nodes;
    
    public Network(int size) {
        this.random = new Random();
        this.size = size;
        this.nodes = new Node[size];
        for (lastId = 0; lastId < size; lastId++) {
            nodes[lastId] = new Node(this, lastId);
        }
    }
    
    public Node[] getRandomNodes(int count) {
        HashSet<Node> randomNodes = new HashSet<Node>(count);
        while (randomNodes.size() < count) {
            int i = random.nextInt(size);
            randomNodes.add(nodes[i]);
        }
        return randomNodes.toArray(new Node[count]);
    }

    public Node[] getAll() {
        return nodes;
    }
    
    public int getNewId() {
        return lastId++;
    }
}
