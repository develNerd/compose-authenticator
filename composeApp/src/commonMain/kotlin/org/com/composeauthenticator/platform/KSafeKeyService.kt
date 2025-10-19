package org.com.composeauthenticator.platform

import eu.anifantakis.lib.ksafe.KSafe
import org.koin.compose.koinInject


class KSafeKeyService(private  val kSafe: KSafe) : KeyService {
    
    private companion object {
        const val KEY_ALIAS = "SavitAuthenticatorEncryptionKey"
    }

    override fun getKey(): CharArray {
        // Try to get existing key from KSafe
        val existingKey = kSafe.getDirect(KEY_ALIAS, "")
        if (existingKey.isNotEmpty()) {
            return existingKey.toCharArray()
        }
        
        // Generate new key if not found
        return generateAndStoreKey()
    }
    
    override fun generateKey(): String? {
        return try {
            generateAndStoreKey()
            "Key generated successfully"
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    private fun generateAndStoreKey(): CharArray {
        // Generate a secure random key
        val keyString = KeyGenerator.generateSecureKey()
        
        // Store securely using KSafe
        kSafe.putDirect(KEY_ALIAS, keyString)
        
        return keyString.toCharArray()
    }
}