package com.secure.privacyfirst.data

import android.util.Base64
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

object CryptoUtils {
    private const val ALGORITHM = "AES/GCM/NoPadding"
    private const val TAG_LENGTH = 128
    private const val IV_LENGTH = 12
    
    /**
     * Generates a simple AES key for demonstration.
     * In production, use Android Keystore for better security.
     */
    fun generateKey(): SecretKey {
        val keyGen = KeyGenerator.getInstance("AES")
        keyGen.init(256)
        return keyGen.generateKey()
    }
    
    /**
     * Encrypts a string using AES-GCM
     */
    fun encrypt(data: String, key: SecretKey): String {
        val cipher = Cipher.getInstance(ALGORITHM)
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val iv = cipher.iv
        val encrypted = cipher.doFinal(data.toByteArray())
        
        // Combine IV and encrypted data
        val combined = iv + encrypted
        return Base64.encodeToString(combined, Base64.DEFAULT)
    }
    
    /**
     * Decrypts a string using AES-GCM
     */
    fun decrypt(encryptedData: String, key: SecretKey): String {
        val combined = Base64.decode(encryptedData, Base64.DEFAULT)
        
        // Extract IV and encrypted data
        val iv = combined.copyOfRange(0, IV_LENGTH)
        val encrypted = combined.copyOfRange(IV_LENGTH, combined.size)
        
        val cipher = Cipher.getInstance(ALGORITHM)
        val spec = GCMParameterSpec(TAG_LENGTH, iv)
        cipher.init(Cipher.DECRYPT_MODE, key, spec)
        
        val decrypted = cipher.doFinal(encrypted)
        return String(decrypted)
    }
    
    /**
     * Simple hash for PIN (for demo purposes)
     * In production, use proper key derivation functions like PBKDF2
     */
    fun hashPin(pin: String): String {
        return Base64.encodeToString(pin.toByteArray(), Base64.DEFAULT)
    }
    
    /**
     * Get a stored key or generate a new one
     * This is simplified - in production, use Android Keystore
     */
    fun getOrCreateKey(): SecretKey {
        // For demo, using a hardcoded key. 
        // TODO: In production, store this in Android Keystore
        val keyBytes = "MySecretKey12345MySecretKey12345".toByteArray().copyOf(32)
        return SecretKeySpec(keyBytes, "AES")
    }
}
