package com.github.louislef299.blouis

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class DeviceExtensionsTest {
    
    @Test
    fun testFormatDeviceName() {
        assertEquals("My Device", DeviceUtils.formatDeviceName("My Device", "00-15-8A-11-79-A0"))
        assertEquals("00:15:8A:11:79:A0", DeviceUtils.formatDeviceName(null, "00-15-8A-11-79-A0"))
        assertEquals("00:15:8A:11:79:A0", DeviceUtils.formatDeviceName("", "00-15-8A-11-79-A0"))
    }
    
    @Test
    fun testFormatMacAddress() {
        assertEquals("00:15:8A:11:79:A0", DeviceUtils.formatMacAddress("00-15-8a-11-79-a0"))
        assertEquals("AA:BB:CC:DD:EE:FF", DeviceUtils.formatMacAddress("aa:bb:cc:dd:ee:ff"))
    }
    
    @Test
    fun testExtractManufacturerHint() {
        assertEquals("11-79-A0", DeviceUtils.extractManufacturerHint("00-15-8A-11-79-A0"))
        assertEquals("79-A0-BB", DeviceUtils.extractManufacturerHint("AA:BB:CC:DD:79:A0:BB"))
    }
    
    @Test
    fun testDeviceInfoDataClass() {
        val device = DeviceInfo(
            name = "Test Device",
            address = "00:11:22:33:44:55",
            manufacturer = "Test Corp",
            rssi = -45,
            paired = true,
            trusted = false
        )
        
        assertEquals("Test Device", device.name)
        assertEquals("00:11:22:33:44:55", device.address)
        assertEquals("Test Corp", device.manufacturer)
        assertEquals(-45, device.rssi)
        assertTrue(device.paired)
        assertFalse(device.trusted)
    }
}