package com.github.louislef299.blouis

data class DeviceInfo(
    val name: String,
    val address: String,
    val manufacturer: String? = null,
    val rssi: Int? = null,
    val paired: Boolean = false,
    val trusted: Boolean = false
)

object DeviceUtils {
    @JvmStatic
    fun formatDeviceName(name: String?, address: String): String {
        return when {
            !name.isNullOrBlank() -> name
            else -> formatMacAddress(address)
        }
    }
    
    @JvmStatic
    fun formatMacAddress(address: String): String {
        return address.replace("-", ":").uppercase()
    }
    
    @JvmStatic
    fun extractManufacturerHint(address: String): String {
        val cleanAddress = address.replace("-", "").replace(":", "")
        return if (cleanAddress.length >= 6) {
            cleanAddress.takeLast(6).chunked(2).joinToString("-")
        } else address
    }
}