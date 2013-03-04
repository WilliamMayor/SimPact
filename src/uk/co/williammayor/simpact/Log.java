package uk.co.williammayor.simpact;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author william
 */
public class Log {

    private BufferedWriter out;
    private String path;

    public Log(String path) throws IOException {
        this.path = path;
        out = new BufferedWriter(new FileWriter(path));
    }

    public void println(String line) {
        try {
            out.write(line + "\n");
        } catch (IOException ex) {
            System.err.println("Error writing to log, " + path);
            System.err.println(line);
        }
    }

    public void reset() {
        try {
            out.close();
        } catch (IOException ex) {
            System.err.println("Could not close log, " + path);
        }
        try {
            out = new BufferedWriter(new FileWriter(path));
        } catch (IOException ex) {
            System.err.println("Could not re-create output writer, " + path);
        }
    }
}
