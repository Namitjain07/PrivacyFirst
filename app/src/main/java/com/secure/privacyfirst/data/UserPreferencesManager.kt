package com.secure.privacyfirst.data

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class UserPreferencesManager(private val context: Context) {
    
    companion object {
        private const val TAG = "UserPreferencesManager"
        private val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        private val SETUP_COMPLETED = booleanPreferencesKey("setup_completed")
        private val USER_NAME = stringPreferencesKey("user_name")
        private val SECURITY_LEVEL = stringPreferencesKey("security_level")
    }
    
    val isOnboardingCompleted: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[ONBOARDING_COMPLETED] ?: false
    }
    
    val isSetupCompleted: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[SETUP_COMPLETED] ?: false
    }
    
    val userName: Flow<String> = context.dataStore.data.map { preferences ->
        val name = preferences[USER_NAME] ?: ""
        Log.d(TAG, "Retrieved userName from DataStore: '$name'")
        name
    }
    
    val securityLevel: Flow<SecurityLevel> = context.dataStore.data.map { preferences ->
        val level = preferences[SECURITY_LEVEL] ?: SecurityLevel.MEDIUM.name
        SecurityLevel.fromString(level)
    }
    
    suspend fun setOnboardingCompleted() {
        context.dataStore.edit { preferences ->
            preferences[ONBOARDING_COMPLETED] = true
        }
    }
    
    suspend fun setSetupCompleted(userName: String) {
        Log.d(TAG, "Saving userName to DataStore: '$userName'")
        context.dataStore.edit { preferences ->
            preferences[SETUP_COMPLETED] = true
            preferences[USER_NAME] = userName
        }
        Log.d(TAG, "userName saved successfully")
    }
    
    suspend fun setSecurityLevel(level: SecurityLevel) {
        context.dataStore.edit { preferences ->
            preferences[SECURITY_LEVEL] = level.name
        }
    }
}
