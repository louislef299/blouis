package com.github.louislef299.blouis.cmd;

import com.github.rvesse.airline.annotations.Command;
import com.github.rvesse.airline.annotations.Option;
import com.github.louislef299.blouis.bluetooth.BluetoothManager;

@Command(name = "scan", description = "Scan for nearby Bluetooth devices")
public class ScanCommand implements Runnable {
    
    @Option(name = {"-t", "--timeout"}, 
            description = "Scan timeout in seconds (default: 10)")
    private int timeout = 10;
    
    @Option(name = {"-v", "--verbose"}, 
            description = "Show verbose output")
    private boolean verbose = false;
    
    @Option(name = {"-h", "--help"}, 
            description = "Show help information")
    private boolean help = false;

    @Override
    public void run() {
        if (help) {
            showHelp();
            return;
        }
        
        if (verbose) {
            System.out.println("Starting Bluetooth device scan...");
            System.out.println("Scan timeout: " + timeout + " seconds");
        }
        
        BluetoothManager bluetoothManager = null;
        try {
            bluetoothManager = new BluetoothManager();
            bluetoothManager.scanForDevices(timeout, verbose);
        } catch (Exception e) {
            System.err.println("Error during Bluetooth scan: " + e.getMessage());
            if (verbose) {
                e.printStackTrace();
            }
            throw new RuntimeException(e);
        } finally {
            if (bluetoothManager != null) {
                bluetoothManager.close();
            }
        }
    }
    
    private void showHelp() {
        System.out.println("blou scan - Scan for nearby Bluetooth devices");
        System.out.println();
        System.out.println("Usage: blou scan [options]");
        System.out.println();
        System.out.println("Options:");
        System.out.println("  -h, --help       Show this help message");
        System.out.println("  -t, --timeout    Scan timeout in seconds (default: 10)");
        System.out.println("  -v, --verbose    Show verbose output");
        System.out.println();
        System.out.println("Examples:");
        System.out.println("  blou scan                    # Scan with default 10 second timeout");
        System.out.println("  blou scan -t 5               # Scan for 5 seconds");
        System.out.println("  blou scan -v -t 15           # Verbose scan for 15 seconds");
        System.out.println();
        System.out.println("Requirements:");
        System.out.println("  - BlueZ service running");
        System.out.println("  - Bluetooth adapter enabled");
        System.out.println("  - Appropriate permissions for D-Bus access");
    }
}