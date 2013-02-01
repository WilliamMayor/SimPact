package uk.co.williammayor.simpact;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class Main {

    public static void main(String[] args) {
        CommandLineParser parser = new GnuParser();
        Options options = new Options();
        options.addOption(new Option("r", true, "The number of nodes a torrent author should bootstrap to"));
        options.addOption(new Option("z", true, "The number of nodes to contact per query"));
        options.addOption(new Option("n", true, "The total number of nodes in the network"));
        try {
            CommandLine line = parser.parse(options, args);
            String[] remaining = line.getArgs();
            if (remaining.length != 1) {
                throw new ParseException("Must provide one experiment to run");
            }
            Class simulatorClass = Class.forName(remaining[0]);
            Simulator simulator = (Simulator) simulatorClass.newInstance();
            simulator.setup(line);
            simulator.run();
        } catch (InstantiationException ex) {
            System.err.println("Could not start experiment: InstantiationException");
        } catch (IllegalAccessException ex) {
            System.err.println("Could not start experiment: IllegalAccessException");
        } catch (ClassNotFoundException ex) {
            System.err.println("Could not start experiment: ClassNotFoundException");
        } catch (IllegalStateException ex) {
            System.err.println("Could not start experiment: incorrect arguments provided");
        } catch (ParseException exp) {
            System.err.println("Parsing failed.  Reason: " + exp.getMessage());
        } 
    }
}
