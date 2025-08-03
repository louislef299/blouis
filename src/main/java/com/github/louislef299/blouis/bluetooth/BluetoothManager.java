package com.github.louislef299.blouis.bluetooth;

import com.github.hypfvieh.bluetooth.DeviceManager;
import com.github.hypfvieh.bluetooth.wrapper.BluetoothAdapter;
import com.github.hypfvieh.bluetooth.wrapper.BluetoothDevice;
import org.freedesktop.dbus.exceptions.DBusException;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class BluetoothManager {
    
    private DeviceManager deviceManager;
    
    public BluetoothManager() throws DBusException {
        this.deviceManager = DeviceManager.createInstance(false);
    }
    
    public void checkSystemRequirements(boolean verbose) throws Exception {
        if (verbose) {
            System.out.println("Checking system requirements...");
            System.out.println("- Checking D-Bus connection...");
        }
        
        try {
            // Try to scan for adapters to verify D-Bus is working
            deviceManager.scanForBluetoothAdapters();
            if (verbose) {
                System.out.println("- D-Bus connection: OK");
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to connect to D-Bus. Make sure BlueZ service is running: " + e.getMessage());
        }
    }
    
    public void scanForDevices(int timeoutSeconds, boolean verbose) throws Exception {
        // Check system requirements first
        checkSystemRequirements(verbose);
        
        if (verbose) {
            System.out.println("Initializing Bluetooth adapter...");
        }
        
        BluetoothAdapter adapter;
        try {
            adapter = deviceManager.getAdapter();
        } catch (IndexOutOfBoundsException e) {
            throw new RuntimeException("No Bluetooth adapter found. Make sure Bluetooth is enabled and BlueZ is running.");
        }
        
        if (adapter == null) {
            throw new RuntimeException("No Bluetooth adapter found. Make sure Bluetooth is enabled.");
        }
        
        if (verbose) {
            System.out.println("Found adapter: " + adapter.getName());
            System.out.println("Adapter address: " + adapter.getAddress());
        }
        
        // Clear any previous scan results
        if (verbose) {
            System.out.println("Starting device discovery...");
        }
        
        // Start discovery
        adapter.startDiscovery();
        
        // Wait for the specified timeout
        Thread.sleep(TimeUnit.SECONDS.toMillis(timeoutSeconds));
        
        // Stop discovery
        adapter.stopDiscovery();
        
        if (verbose) {
            System.out.println("Discovery completed. Found devices:");
        }
        
        // Get discovered devices
        List<BluetoothDevice> devices = deviceManager.getDevices();
        
        if (devices.isEmpty()) {
            System.out.println("No devices found.");
        } else {
            System.out.println("Found " + devices.size() + " device(s):");
            for (BluetoothDevice device : devices) {
                displayDevice(device, verbose);
            }
        }
    }
    
    private void displayDevice(BluetoothDevice device, boolean verbose) {
        String name = device.getName();
        String address = device.getAddress();
        
        // Display basic info
        System.out.println("  " + (name != null ? name : "[Unknown Device]") + " (" + address + ")");
        
        if (verbose) {
            try {
                Short rssi = device.getRssi();
                if (rssi != null) {
                    System.out.println("    RSSI: " + rssi + " dBm");
                }
                System.out.println("    Paired: " + device.isPaired());
                System.out.println("    Connected: " + device.isConnected());
                System.out.println("    Trusted: " + device.isTrusted());
                
                String[] uuids = device.getUuids();
                if (uuids != null && uuids.length > 0) {
                    System.out.println("    Services: " + uuids.length + " available");
                }
            } catch (Exception e) {
                // Some properties might not be available for all devices
                if (verbose) {
                    System.out.println("    (Some device details unavailable)");
                }
            }
        }
    }
    
    public void close() {
        if (deviceManager != null) {
            try {
                deviceManager.closeConnection();
            } catch (Exception e) {
                System.err.println("Error closing Bluetooth connection: " + e.getMessage());
            }
        }
    }
}