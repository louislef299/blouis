package com.github.louislef299.blouis.cmd

import com.github.rvesse.airline.annotations.Command
import com.github.rvesse.airline.annotations.Option
import com.github.louislef299.blouis.bluetooth.BluetoothManager
import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import org.slf4j.LoggerFactory

@Command(name = "devices", description = "List paired, trusted, and connected Bluetooth devices")
class DevicesCommand : Runnable {
    
    @Option(name = ["-v", "--verbose"], 
            description = "Show verbose device information")
    private var verbose: Boolean = false
    
    @Option(name = ["-h", "--help"], 
            description = "Show help information")
    private var help: Boolean = false
    
    @Option(name = ["-d", "--debug"], 
            description = "Enable debug logging")
    private var debug: Boolean = false
    
    @Option(name = ["-p", "--paired"], 
            description = "Show only paired devices")
    private var pairedOnly: Boolean = false
    
    @Option(name = ["-c", "--connected"], 
            description = "Show only connected devices")
    private var connectedOnly: Boolean = false
    
    @Option(name = ["-t", "--trusted"], 
            description = "Show only trusted devices")
    private var trustedOnly: Boolean = false

    override fun run() {
        if (help) {
            showHelp()
            return
        }
        
        // Enable debug logging if requested
        if (debug) {
            val root = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME) as Logger
            root.level = Level.DEBUG
            val bluetoothLogger = LoggerFactory.getLogger("com.github.hypfvieh.bluetooth") as Logger
            bluetoothLogger.level = Level.DEBUG
        }
        
        if (verbose) {
            println("Listing Bluetooth devices...")
            val filters = mutableListOf<String>()
            if (pairedOnly) filters.add("paired")
            if (connectedOnly) filters.add("connected") 
            if (trustedOnly) filters.add("trusted")
            if (filters.isNotEmpty()) {
                println("Filters: ${filters.joinToString(", ")}")
            }
        }
        
        var bluetoothManager: BluetoothManager? = null
        try {
            bluetoothManager = BluetoothManager()
            listDevices(bluetoothManager)
        } catch (e: Exception) {
            System.err.println("Error listing Bluetooth devices: ${e.message}")
            if (verbose) {
                e.printStackTrace()
            }
            throw RuntimeException(e)
        } finally {
            bluetoothManager?.close()
        }
    }
    
    private fun listDevices(bluetoothManager: BluetoothManager) {
        // Check system requirements first
        bluetoothManager.checkSystemRequirements(verbose)
        
        if (verbose) {
            println("Getting device list from BlueZ...")
        }
        
        // Get all known devices (not just discovered ones)
        val deviceManager = bluetoothManager.getDeviceManager()
        val adapter = deviceManager.adapter ?: run {
            throw RuntimeException("No Bluetooth adapter found. Make sure Bluetooth is enabled.")
        }
        
        if (verbose) {
            println("Using adapter: ${adapter.name}")
            println("Adapter address: ${adapter.address}")
        }
        
        val allDevices = deviceManager.devices
        
        // Apply filters
        val filteredDevices = allDevices.filter { device ->
            when {
                pairedOnly && !device.isPaired -> false
                connectedOnly && !device.isConnected -> false  
                trustedOnly && !device.isTrusted -> false
                else -> true
            }
        }
        
        if (filteredDevices.isEmpty()) {
            val filterDescription = when {
                pairedOnly -> "paired "
                connectedOnly -> "connected "
                trustedOnly -> "trusted "
                else -> ""
            }
            println("No ${filterDescription}devices found.")
            
            if (!pairedOnly && !connectedOnly && !trustedOnly) {
                println("Try running 'blou scan' to discover nearby devices first.")
            }
        } else {
            val deviceType = when {
                pairedOnly -> "paired"
                connectedOnly -> "connected"
                trustedOnly -> "trusted"
                else -> "known"
            }
            println("Found ${filteredDevices.size} $deviceType device(s):")
            
            filteredDevices.forEach { device ->
                displayDevice(device)
            }
        }
    }
    
    private fun displayDevice(device: com.github.hypfvieh.bluetooth.wrapper.BluetoothDevice) {
        val name = device.name
        val alias = device.alias
        val address = device.address
        
        // Try to get the best available name (reusing Java logic)
        val displayName = getBestDeviceName(name, alias, address)
        
        // Status indicators
        val status = mutableListOf<String>()
        if (device.isConnected) status.add("Connected")
        if (device.isPaired) status.add("Paired")
        if (device.isTrusted) status.add("Trusted")
        
        val statusStr = if (status.isNotEmpty()) " [${status.joinToString(", ")}]" else ""
        
        println("  $displayName ($address)$statusStr")
        
        if (verbose) {
            try {
                device.rssi?.let { rssi ->
                    println("    RSSI: $rssi dBm")
                }
                
                device.uuids?.let { uuids ->
                    if (uuids.isNotEmpty()) {
                        println("    Services: ${uuids.size} available")
                        if (debug) {
                            uuids.take(3).forEach { uuid ->
                                println("      - $uuid")
                            }
                            if (uuids.size > 3) {
                                println("      ... and ${uuids.size - 3} more")
                            }
                        }
                    }
                }
                
                // Note: modalias property may not be available in all versions
                try {
                    val modalias = device.javaClass.getMethod("getModalias").invoke(device) as? String
                    modalias?.let { 
                        println("    Device Type: $it")
                    }
                } catch (e: Exception) {
                    // modalias not available in this version
                }
                
            } catch (e: Exception) {
                // Some properties might not be available for all devices
                if (debug) {
                    println("    (Some device details unavailable: ${e.message})")
                }
            }
        }
    }
    
    // Kotlin version of the Java method - showing both approaches for learning
    private fun getBestDeviceName(name: String?, alias: String?, address: String): String {
        // Priority: name > alias > formatted address
        return when {
            !name.isNullOrBlank() -> name
            !alias.isNullOrBlank() && alias != address -> alias
            else -> formatMacAddress(address)
        }
    }
    
    private fun formatMacAddress(address: String?): String {
        if (address == null) return "[Unknown Device]"
        
        // Convert to standard MAC format if needed  
        val formattedMac = address.uppercase().replace("-", ":")
        
        // Ensure we have a proper MAC address format
        if (formattedMac.length < 17) return "[Unknown Device]"
        
        // Try to identify manufacturer from OUI (first 3 octets)
        val oui = formattedMac.substring(0, 8) // First 3 octets: XX:XX:XX
        val manufacturer = getManufacturerFromOUI(oui)
        
        return if (manufacturer != null) {
            "$manufacturer Device (${formattedMac.substring(9)})" // Show last 3 octets
        } else {
            "[Device ${formattedMac.substring(9)}]" // Show last 3 octets only
        }
    }
    
    // Kotlin version showing more idiomatic approach with when expression
    private fun getManufacturerFromOUI(oui: String): String? = when (oui) {
        "00:15:8A" -> "Garmin"          // GPS/fitness devices
        "00:C5:85" -> "Apple"           // Apple devices  
        "80:16:09" -> "Sleep Number"    // Smart beds
        "CC:6A:10" -> "Chamberlain"     // MyQ garage door
        "C4:35:34" -> "Govee"           // Smart lighting
        "3C:95:09" -> "Intel"           // Bluetooth adapters
        "B8:27:EB", "DC:A6:32", "E4:5F:01" -> "Raspberry Pi"
        "B8:AE:ED", "50:F5:DA", "AC:63:BE" -> "Amazon" // Echo devices
        else -> null
    }
    
    private fun showHelp() {
        println("blou devices - List paired, trusted, and connected Bluetooth devices")
        println()
        println("Usage: blou devices [options]")
        println()
        println("Options:")
        println("  -c, --connected  Show only connected devices")
        println("  -d, --debug      Enable debug logging")
        println("  -h, --help       Show this help message")
        println("  -p, --paired     Show only paired devices")  
        println("  -t, --trusted    Show only trusted devices")
        println("  -v, --verbose    Show verbose device information")
        println()
        println("Examples:")
        println("  blou devices                 # List all known devices")
        println("  blou devices -p              # Show only paired devices")
        println("  blou devices -c -v           # Show connected devices with details")
        println("  blou devices -t --debug      # Show trusted devices with debug info")
        println()
        println("Note:")
        println("  This command shows devices that have been previously discovered,")
        println("  paired, or connected. Use 'blou scan' to find new nearby devices.")
        println()
        println("Requirements:")
        println("  - BlueZ service running")
        println("  - Bluetooth adapter enabled")
        println("  - Appropriate permissions for D-Bus access")
    }
}