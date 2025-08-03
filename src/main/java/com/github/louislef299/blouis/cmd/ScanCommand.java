package com.github.louislef299.blouis.cmd;

import com.github.rvesse.airline.annotations.Command;
import com.github.rvesse.airline.annotations.Option;
import com.github.louislef299.blouis.bluetooth.BluetoothManager;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;

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
    
    @Option(name = {"-d", "--debug"}, 
            description = "Enable debug logging")
    private boolean debug = false;
    
    @Option(name = {"-n", "--names"}, 
            description = "Attempt to resolve device names (longer scan)")
    private boolean resolveNames = false;

    @Override
    public void run() {
        if (help) {
            showHelp();
            return;
        }
        
        // Enable debug logging if requested
        if (debug) {
            Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
            root.setLevel(Level.DEBUG);
            Logger bluetoothLogger = (Logger) LoggerFactory.getLogger("com.github.hypfvieh.bluetooth");
            bluetoothLogger.setLevel(Level.DEBUG);
        }
        
        if (verbose) {
            System.out.println("Starting Bluetooth device scan...");
            System.out.println("Scan timeout: " + timeout + " seconds");
        }
        
        BluetoothManager bluetoothManager = null;
        try {
            bluetoothManager = new BluetoothManager();
            if (resolveNames && timeout < 15) {
                if (verbose) {
                    System.out.println("Name resolution enabled - extending timeout to 15 seconds");
                }
                bluetoothManager.scanForDevices(15, verbose);
            } else {
                bluetoothManager.scanForDevices(timeout, verbose);
            }
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
        System.out.println("  -d, --debug      Enable debug logging");
        System.out.println("  -h, --help       Show this help message");
        System.out.println("  -n, --names      Attempt to resolve device names (longer scan)");
        System.out.println("  -t, --timeout    Scan timeout in seconds (default: 10)");
        System.out.println("  -v, --verbose    Show verbose output");
        System.out.println();
        System.out.println("Examples:");
        System.out.println("  blou scan                    # Scan with default 10 second timeout");
        System.out.println("  blou scan -t 5               # Scan for 5 seconds");
        System.out.println("  blou scan -n -v              # Try to resolve names with longer scan");
        System.out.println("  blou scan -d -v -t 10        # Debug + verbose scan for troubleshooting");
        System.out.println();
        System.out.println("Requirements:");
        System.out.println("  - BlueZ service running");
        System.out.println("  - Bluetooth adapter enabled");
        System.out.println("  - Appropriate permissions for D-Bus access");
    }
}