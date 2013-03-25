package uk.co.williammayor.simpact;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author william
 */
public class Log {

    private boolean first;
    private BufferedWriter out;
    private String name;

    public Log(String name) throws IOException {
        this.name = name;
        first = true;
        out = makeWriter(name, first);
    }

    public void println(String line) {
        if (null == out) {
            out = makeWriter(name, first);
        }
        try {
            out.write(line + "\n");
        } catch (IOException ex) {
            System.err.println("Error writing to log, " + name);
            System.err.println(line);
        }
    }

    public void reset() {
        try {
            out.close();
        } catch (IOException ex) {
            System.err.println("Could not close log, " + name);
        }
        out = null;
        first = !first;
    }
    
    private BufferedWriter makeWriter(String name, boolean first) {
        try {
            String path = name + "-" + (first ? "1" : "2") + ".txt";
            return new BufferedWriter(new FileWriter(path));
        } catch (IOException ex) {
            System.err.println("Could not re-create output writer, " + name);
            return null;
        }
    }
}
