package com.github.louislef299.blouis;

import com.github.rvesse.airline.Cli;
import com.github.rvesse.airline.builder.CliBuilder;
import com.github.rvesse.airline.annotations.Command;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class App {
    
    @Command(name = "version", description = "Show version information")
    public static class VersionCommand implements Runnable {
        @Override
        public void run() {
            String version = getVersion();
            System.out.println("blou version " + version);
        }
        
        private String getVersion() {
            try (InputStream input = getClass().getClassLoader().getResourceAsStream("version.properties")) {
                if (input == null) {
                    return "unknown";
                }
                Properties props = new Properties();
                props.load(input);
                return props.getProperty("version", "unknown");
            } catch (IOException e) {
                return "unknown";
            }
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
