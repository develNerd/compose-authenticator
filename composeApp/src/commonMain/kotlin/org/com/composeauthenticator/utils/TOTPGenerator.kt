package org.com.composeauthenticator.utils

import dev.whyoleg.cryptography.CryptographyProvider
import dev.whyoleg.cryptography.DelicateCryptographyApi
import dev.whyoleg.cryptography.algorithms.HMAC
import dev.whyoleg.cryptography.algorithms.SHA1
import kotlin.math.pow
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/**
 * TOTP (Time-based One-Time Password) Generator
 * Implementation follows RFC 6238 standard
 */
class TOTPGenerator {
    
    companion object {
        private const val DIGITS = 6
        private const val TIME_STEP = 30L
        private const val EPOCH_START = 0L
        
        private val cryptographyProvider = CryptographyProvider.Default
        private val hmac = cryptographyProvider.get(HMAC)
        
        /**
         * Generate a TOTP code for the given secret and current time
         * @param secret Base32 encoded secret key
         * @param currentTime Current Unix timestamp in seconds (default: current system time)
         * @return 6-digit TOTP code as string
         */
        @OptIn(ExperimentalTime::class)
        suspend fun generateTOTP(secret: String, currentTime: Long = Clock.System.now().toEpochMilliseconds() / 1000): String {
            val timeStep = (currentTime - EPOCH_START) / TIME_STEP
            return generateHOTP(secret, timeStep)
        }
        
        /**
         * Generate an HOTP code for the given secret and counter
         * @param secret Base32 encoded secret key
         * @param counter Counter value (for TOTP, this is the time step)
         * @return 6-digit HOTP code as string
         */
        @OptIn(DelicateCryptographyApi::class)
        suspend fun generateHOTP(secret: String, counter: Long): String {
            try {
                val decodedSecret = base32Decode(secret)
                val counterBytes = longToByteArray(counter)
                
                val hmacKey = hmac.keyDecoder(SHA1).decodeFromByteArray(HMAC.Key.Format.RAW, decodedSecret)
                val signature = hmacKey.signatureGenerator().generateSignature(counterBytes)
                
                val hash = signature
                val offset = hash[hash.size - 1].toInt() and 0x0F
                
                // Dynamic truncation as per RFC
                val truncatedHash = ((hash[offset].toInt() and 0x7F) shl 24) or
                        ((hash[offset + 1].toInt() and 0xFF) shl 16) or
                        ((hash[offset + 2].toInt() and 0xFF) shl 8) or
                        (hash[offset + 3].toInt() and 0xFF)
                
                val otp = truncatedHash % 1000000 // 10^6 for 6 digits
                return otp.toString().padStart(DIGITS, '0')
            } catch (e: Exception) {
                // Return error code if generation fails
                return "ERROR"
            }
        }
        
        /**
         * Convert Long to ByteArray (big-endian, 8 bytes)
         */
        private fun longToByteArray(value: Long): ByteArray {
            return byteArrayOf(
                (value shr 56).toByte(),
                (value shr 48).toByte(),
                (value shr 40).toByte(),
                (value shr 32).toByte(),
                (value shr 24).toByte(),
                (value shr 16).toByte(),
                (value shr 8).toByte(),
                value.toByte()
            )
        }
        
        /**
         * Get remaining time in seconds until next TOTP code generation
         * @param currentTime Current Unix timestamp in seconds (default: current system time)
         * @return Remaining seconds (0-29)
         */
        @OptIn(ExperimentalTime::class)
        fun getRemainingTime(currentTime: Long = Clock.System.now().toEpochMilliseconds() / 1000): Long {
            return TIME_STEP - (currentTime % TIME_STEP)
        }


        /**
         * Decode Base32 string to byte array
         * @param encoded Base32 encoded string
         * @return Decoded byte array
         * @throws IllegalArgumentException if input contains invalid characters
         */
        private fun base32Decode(encoded: String): ByteArray {
            if (encoded.isEmpty()) {
                return ByteArray(0)
            }
            
            // Clean input: remove padding and convert to uppercase
            val cleanInput = encoded.uppercase().replace("=", "").replace(" ", "")
            val alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567"
            val bytes = mutableListOf<Byte>()
            
            var buffer = 0
            var bitsLeft = 0
            
            for (char in cleanInput) {
                val value = alphabet.indexOf(char)
                if (value == -1) {
                    throw IllegalArgumentException("Invalid character in Base32 string: $char")
                }
                
                buffer = (buffer shl 5) or value
                bitsLeft += 5
                
                if (bitsLeft >= 8) {
                    bytes.add((buffer shr (bitsLeft - 8)).toByte())
                    bitsLeft -= 8
                }
            }
            
            return bytes.toByteArray()
        }
    }
}