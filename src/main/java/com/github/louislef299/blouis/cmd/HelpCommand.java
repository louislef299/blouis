package com.github.louislef299.blouis.cmd;

import com.github.rvesse.airline.annotations.Command;

@Command(name = "help", description = "Show help information")
public class HelpCommand implements Runnable {
    @Override
    public void run() {
        System.out.println("blou - A CLI tool");
        System.out.println();
        System.out.println("Usage: blou <command> [options]");
        System.out.println();
        System.out.println("Available commands:");
        System.out.println("  devices    List paired, trusted, and connected Bluetooth devices");
        System.out.println("  help       Show this help message");
        System.out.println("  scan       Scan for nearby Bluetooth devices");
        System.out.println("  version    Show version information");
        System.out.println();
        System.out.println("Use 'blou <command> --help' for more information about a specific command.");
    }
}