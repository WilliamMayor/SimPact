package uk.co.williammayor.simpact.awareness;

import java.util.Random;
import org.apache.commons.cli.CommandLine;
import uk.co.williammayor.simpact.Simulator;

public class Awareness implements Simulator {

    private Random random;
    private Node[] nodes;
    private int z;
    private int r;
    private int n;
    private int aware;

    public int getZ() {
        return z;
    }

    public int getR() {
        return r;
    }

    public int getN() {
        return n;
    }

    public void incrementAware() {
        aware++;
    }

    public Node[] getRandomNodes(int count, int notMe) {
        int[] indicies = indicies(count, notMe);
        Node[] randomNodes = new Node[count];
        for (int i = 0; i < count; i++) {
            randomNodes[i] = nodes[indicies[i]];
        }
        return randomNodes;
    }

    private boolean contains(int[] values, int value, int currentIndex) {
        for (int i = 0; i < currentIndex; i++) {
            if (values[i] == value) {
                return true;
            }
        }
        return false;
    }

    private int[] indicies(int count, int notThis) {
        int[] indicies = new int[count];
        for (int i = 0; i < count; i++) {
            int next = 0;
            do {
                next = random.nextInt(n);
            } while (contains(indicies, next, i) || next == notThis);
            indicies[i] = next;
        }
        return indicies;
    }

    private void bootstrap() {
        Node node = nodes[0];
        node.author();
        node.bootstrap(r);
    }

    private void simulate() {
        for (Node node : nodes) {
            if (aware != n) {
                node.find(z);
            }
            System.out.println(aware);
        }
    }

    private int getOption(CommandLine line, String option) throws IllegalStateException {
        String s = line.getOptionValue(option);
        if (null != s) {
            return Integer.parseInt(s);
        } else {
            throw new IllegalStateException();
        }
    }

    @Override
    public void setup(CommandLine line) throws IllegalStateException {
        this.z = getOption(line, "z");
        this.r = getOption(line, "r");
        this.n = getOption(line, "n");
        this.aware = 0;
        this.random = new Random();
        this.nodes = new Node[n];
        for (int i = 0; i < n; i++) {
            nodes[i] = new Node(this, i);
        }
    }

    @Override
    public void run() {
        System.out.println("    Nodes: " + n);
        System.out.println("        Z: " + z);
        System.out.println("Bootstrap: " + r);
        bootstrap();
        simulate();
    }
}
