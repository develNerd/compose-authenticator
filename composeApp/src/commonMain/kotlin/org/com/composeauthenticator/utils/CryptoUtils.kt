package org.com.composeauthenticator.utils

import dev.whyoleg.cryptography.CryptographyProvider
import dev.whyoleg.cryptography.algorithms.AES
import dev.whyoleg.cryptography.algorithms.AES.GCM
import dev.whyoleg.cryptography.random.CryptographyRandom
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalEncodingApi::class)
class CryptoUtils {
    
    companion object {
        private val cryptographyProvider = CryptographyProvider.Default
        private val aes = cryptographyProvider.get(AES.GCM)
        private val random = CryptographyRandom
        
        suspend fun encrypt(data: String, key: CharArray): String {
            val keyBytes = hexStringToByteArray(key.concatToString())
            val aesKey = aes.keyDecoder().decodeFromByteArray(AES.Key.Format.RAW, keyBytes)
            
            val cipher = aesKey.cipher()
            val plaintext = data.encodeToByteArray()
            val ciphertext = cipher.encrypt(plaintext)
            
            return Base64.encode(ciphertext)
        }
        
        suspend fun decrypt(encryptedData: String, key: CharArray): String {
            val keyBytes = hexStringToByteArray(key.concatToString())
            val aesKey = aes.keyDecoder().decodeFromByteArray(AES.Key.Format.RAW, keyBytes)
            
            val cipher = aesKey.cipher()
            val ciphertext = Base64.decode(encryptedData)
            val plaintext = cipher.decrypt(ciphertext)
            
            return plaintext.decodeToString()
        }
        
        fun hexStringToByteArray(hexString: String): ByteArray {
            val cleanHex = hexString.replace(" ", "")
            require(cleanHex.length % 2 == 0) { "Hex string must have even length" }
            
            return cleanHex.chunked(2)
                .map { it.toInt(16).toByte() }
                .toByteArray()
        }
        
        fun generateRandomSecret(): String {
            val characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567"
            return (1..32)
                .map { characters[random.nextInt(characters.length)] }
                .joinToString("")
        }
    }
}