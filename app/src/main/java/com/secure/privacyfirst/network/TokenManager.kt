package com.secure.privacyfirst.network

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

val Context.tokenDataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_token")

class TokenManager(private val context: Context) {
    
    companion object {
        private val JWT_TOKEN = stringPreferencesKey("jwt_token")
        private val TOKEN_EXPIRY = longPreferencesKey("token_expiry")
    }
    
    val token: Flow<String?> = context.tokenDataStore.data.map { preferences ->
        preferences[JWT_TOKEN]
    }
    
    val tokenExpiry: Flow<Long> = context.tokenDataStore.data.map { preferences ->
        preferences[TOKEN_EXPIRY] ?: 0L
    }
    
    suspend fun saveToken(token: String, expiresIn: String = "1h") {
        val expiryTime = System.currentTimeMillis() + parseExpiryTime(expiresIn)
        context.tokenDataStore.edit { preferences ->
            preferences[JWT_TOKEN] = token
            preferences[TOKEN_EXPIRY] = expiryTime
        }
    }
    
    suspend fun clearToken() {
        context.tokenDataStore.edit { preferences ->
            preferences.remove(JWT_TOKEN)
            preferences.remove(TOKEN_EXPIRY)
        }
    }
    
    suspend fun getToken(): String? {
        return token.first()
    }
    
    suspend fun isTokenValid(): Boolean {
        val currentToken = token.first()
        val expiry = tokenExpiry.first()
        return currentToken != null && System.currentTimeMillis() < expiry
    }
    
    suspend fun getAuthHeader(): String? {
        val currentToken = getToken()
        return currentToken?.let { "Bearer $it" }
    }
    
    private fun parseExpiryTime(expiresIn: String): Long {
        // Parse expiry time like "1h", "30m", etc.
        return when {
            expiresIn.endsWith("h") -> {
                val hours = expiresIn.dropLast(1).toLongOrNull() ?: 1
                hours * 60 * 60 * 1000 // Convert to milliseconds
            }
            expiresIn.endsWith("m") -> {
                val minutes = expiresIn.dropLast(1).toLongOrNull() ?: 60
                minutes * 60 * 1000
            }
            expiresIn.endsWith("s") -> {
                val seconds = expiresIn.dropLast(1).toLongOrNull() ?: 3600
                seconds * 1000
            }
            else -> 60 * 60 * 1000 // Default to 1 hour
        }
    }
}
