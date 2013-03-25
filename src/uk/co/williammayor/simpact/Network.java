package uk.co.williammayor.simpact;

import java.util.HashSet;
import java.util.Random;
/**
 * Represents the P2P network being simulated.
 * It has a fixed size, when nodes leave they are immediately replaced by a
 * new node.
 * @author william
 */
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
            nodes[lastId] = new Node(lastId, lastId);
        }
    }
    /**
     * Return an array of nodes randomly sampled from the network.
     * Uses a uniform random sample.
     * @param count The sample size
     * @return A list of randomly sampled Nodes
     */
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
    /**
     * Removes the node from the network and immediately replaces it with a
     * fresh one. The two nodes are not the same, they do not share any 
     * data beyond their index in the network.
     * @param node The node to be removed from the network
     */
    public void replace(Node node) {
        nodes[node.getPosition()] = new Node(++lastId, node.getPosition());
    }
}
