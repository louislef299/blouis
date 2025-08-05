package com.github.louislef299.blouis;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class InteropExampleTest {
    
    @Test
    public void testJavaCanUseKotlinUtils() {
        // Java code calling Kotlin static methods (via @JvmStatic)
        String formattedName = DeviceUtils.formatDeviceName("My Device", "00-15-8A-11-79-A0");
        assertEquals("My Device", formattedName);
        
        String formattedMac = DeviceUtils.formatMacAddress("00-15-8a-11-79-a0");
        assertEquals("00:15:8A:11:79:A0", formattedMac);
        
        String hint = DeviceUtils.extractManufacturerHint("00-15-8A-11-79-A0");
        assertEquals("11-79-A0", hint);
    }
    
    @Test
    public void testJavaCanUseKotlinDataClass() {
        // Java code creating and using Kotlin data class
        DeviceInfo device = new DeviceInfo(
            "Test Device",
            "00:11:22:33:44:55", 
            "Apple", 
            -50, 
            true, 
            false
        );
        
        assertEquals("Test Device", device.getName());
        assertEquals("00:11:22:33:44:55", device.getAddress());
        assertEquals("Apple", device.getManufacturer());
        assertEquals(Integer.valueOf(-50), device.getRssi());
        assertTrue(device.getPaired());
        assertFalse(device.getTrusted());
        
        // Kotlin data classes automatically generate toString, equals, hashCode
        assertTrue(device.toString().contains("Test Device"));
        assertTrue(device.toString().contains("Apple"));
    }
}