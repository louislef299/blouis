package com.github.louislef299.blouis;

import com.github.rvesse.airline.Cli;
import com.github.rvesse.airline.builder.CliBuilder;
import com.github.rvesse.airline.annotations.Command;

public class App {
    
    @Command(name = "version", description = "Show version information")
    public static class VersionCommand implements Runnable {
        @Override
        public void run() {
            System.out.println("blou version 1.0-SNAPSHOT");
        }
    }
    
    public static void main(String[] args) {
        CliBuilder<Runnable> builder = Cli.<Runnable>builder("blou")
                .withDescription("Blou CLI tool")
                .withCommand(VersionCommand.class);
        
        Cli<Runnable> cli = builder.build();
        
        try {
            Runnable command = cli.parse(args);
            command.run();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }
}
