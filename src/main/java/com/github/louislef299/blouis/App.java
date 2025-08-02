package com.github.louislef299.blouis;

import com.github.rvesse.airline.Cli;
import com.github.louislef299.blouis.builder.BlouCliBuilder;

public class App {
    public static void main(String[] args) {
        Cli<Runnable> cli = BlouCliBuilder.createDefaultCli();
        
        try {
            Runnable command = cli.parse(args);
            command.run();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }
}
