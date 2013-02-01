package uk.co.williammayor.simpact;

import org.apache.commons.cli.CommandLine;

public interface Simulator {
    public void run();
    public void setup(CommandLine line) throws IllegalStateException;
}
