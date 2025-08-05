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
    
    public DeviceManager getDeviceManager() {
        return deviceManager;
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
        String alias = device.getAlias();
        String address = device.getAddress();
        
        // Try to get the best available name
        String displayName = getBestDeviceName(name, alias, address);
        
        // Display basic info
        System.out.println("  " + displayName + " (" + address + ")");
        
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
    
    private String getBestDeviceName(String name, String alias, String address) {
        // Priority: name > alias > formatted address
        if (name != null && !name.trim().isEmpty()) {
            return name;
        }
        if (alias != null && !alias.trim().isEmpty() && !alias.equals(address)) {
            return alias;
        }
        
        // If no name available, try to make MAC address more readable
        return formatMacAddress(address);
    }
    
    private String formatMacAddress(String address) {
        if (address == null) return "[Unknown Device]";
        
        // Convert to standard MAC format if needed
        String formattedMac = address.toUpperCase().replace("-", ":");
        
        // Ensure we have a proper MAC address format
        if (formattedMac.length() < 17) return "[Unknown Device]";
        
        // Try to identify manufacturer from OUI (first 3 octets)
        String oui = formattedMac.substring(0, 8); // First 3 octets: XX:XX:XX
        String manufacturer = getManufacturerFromOUI(oui);
        
        if (manufacturer != null) {
            return manufacturer + " Device (" + formattedMac.substring(9) + ")"; // Show last 3 octets
        }
        
        return "[Device " + formattedMac.substring(9) + "]"; // Show last 3 octets only
    }
    
    private String getManufacturerFromOUI(String oui) {
        // Common Bluetooth device manufacturers (partial list)
        switch (oui) {
            case "00:15:8A": return "Garmin"; // GPS/fitness devices
            case "00:C5:85": return "Apple"; // Apple devices  
            case "80:16:09": return "Sleep Number"; // Smart beds
            case "CC:6A:10": return "Chamberlain"; // MyQ garage door
            case "C4:35:34": return "Govee"; // Smart lighting
            case "3C:95:09": return "Intel"; // Bluetooth adapters
            case "B8:27:EB": return "Raspberry Pi"; 
            case "DC:A6:32": return "Raspberry Pi";
            case "E4:5F:01": return "Raspberry Pi";
            case "B8:AE:ED": return "Amazon"; // Echo devices
            case "50:F5:DA": return "Amazon"; // Echo devices
            case "AC:63:BE": return "Amazon"; // Echo devices
            default: return null;
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