package com.example.myapplication.data.network.client

/**
 * Global Network Configuration for TruthLens.
 *
 * NOTE FOR EMULATOR VS PHYSICAL DEVICE:
 * - When testing on the official Android Emulator, use: "http://10.0.2.2/Truthlense_backened/"
 *   (10.0.2.2 routes directly to 127.0.0.1 on the host development machine).
 * - When testing on a physical Android device connected via Wi-Fi/USB, change this to your
 *   PC's local LAN IPv4 address (e.g., "http://192.168.1.100/Truthlense_backened/").
 */
object NetworkConfig {

    const val DEFAULT_EMULATOR_BASE_URL = "http://10.0.2.2/Truthlense_backened/"
    const val LOCALHOST_BASE_URL = "http://localhost/Truthlense_backened/"

    @Volatile
    var currentBaseUrl: String = DEFAULT_EMULATOR_BASE_URL

    const val CONNECT_TIMEOUT_SECONDS = 30L
    const val READ_TIMEOUT_SECONDS = 30L
    const val WRITE_TIMEOUT_SECONDS = 30L

    fun setCustomBaseUrl(url: String) {
        val trimmed = url.trim()
        currentBaseUrl = if (trimmed.endsWith("/")) trimmed else "$trimmed/"
    }
}

