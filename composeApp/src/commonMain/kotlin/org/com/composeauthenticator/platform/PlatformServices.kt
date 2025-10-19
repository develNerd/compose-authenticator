package org.com.composeauthenticator.platform

import kotlin.random.Random

// Simplified Key generation and encryption service
interface KeyService {
    fun getKey(): CharArray
    fun generateKey(): String?
}

// Simple key generator utility
object KeyGenerator {
    fun generateSecureKey(): String {
        // Generate a random 32-byte key (256 bits)
        val keyBytes = ByteArray(32)
        repeat(32) { i ->
            keyBytes[i] = Random.nextInt(256).toByte()
        }
        
        // Convert to hex string for storage
        return keyBytes.joinToString("") { byte ->
            val unsigned = byte.toInt() and 0xFF
            unsigned.toString(16).padStart(2, '0')
        }
    }
}



sealed class QRCodeResult {
    data class Success(val qrCode: String) : QRCodeResult()
    data class Error(val message: String) : QRCodeResult()
    object Cancelled : QRCodeResult()
}

// Camera permission service
interface CameraPermissionService {
    suspend fun requestPermission(): Boolean
    suspend fun hasPermission(): Boolean
}