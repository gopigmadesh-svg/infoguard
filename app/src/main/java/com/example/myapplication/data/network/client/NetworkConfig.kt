package com.example.myapplication.data.network.client

import android.os.Build

/**
 * Global Network Configuration for TruthLens.
 *
 * Automatically selects the appropriate backend URL:
 * - When running on official Android Emulator: uses "http://10.0.2.2/Truthlense_backened/"
 * - When running on a physical Android device via USB: uses "http://127.0.0.1/Truthlense_backened/" (paired with `adb reverse tcp:80 tcp:80`)
 * - Can also be updated dynamically at runtime via `setCustomBaseUrl()`.
 */
object NetworkConfig {

    const val DEFAULT_USB_BASE_URL = "http://127.0.0.1/Truthlense_backened/"
    const val DEFAULT_EMULATOR_BASE_URL = "http://10.0.2.2/Truthlense_backened/"
    const val LOCALHOST_BASE_URL = "http://127.0.0.1/Truthlense_backened/"

    val isEmulator: Boolean by lazy {
        Build.FINGERPRINT.startsWith("generic") ||
        Build.FINGERPRINT.startsWith("unknown") ||
        Build.MODEL.contains("google_sdk") ||
        Build.MODEL.contains("Emulator") ||
        Build.MODEL.contains("Android SDK built for x86") ||
        Build.MANUFACTURER.contains("Genymotion") ||
        (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic")) ||
        "google_sdk" == Build.PRODUCT
    }

    @Volatile
    var currentBaseUrl: String = if (isEmulator) DEFAULT_EMULATOR_BASE_URL else DEFAULT_USB_BASE_URL

    const val CONNECT_TIMEOUT_SECONDS = 30L
    const val READ_TIMEOUT_SECONDS = 30L
    const val WRITE_TIMEOUT_SECONDS = 30L

    fun setCustomBaseUrl(url: String) {
        val trimmed = url.trim()
        currentBaseUrl = if (trimmed.endsWith("/")) trimmed else "$trimmed/"
    }
}
