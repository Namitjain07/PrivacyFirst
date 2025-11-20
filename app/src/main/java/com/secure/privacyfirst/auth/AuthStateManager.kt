package com.secure.privacyfirst.auth

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Singleton to manage authentication state across the app
 * Tracks whether user needs to re-authenticate when returning from background
 */
object AuthStateManager {
    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()
    
    private val _requiresAuth = MutableStateFlow(false)
    val requiresAuth: StateFlow<Boolean> = _requiresAuth.asStateFlow()
    
    /**
     * Mark user as authenticated (after successful PIN entry)
     */
    fun setAuthenticated(authenticated: Boolean) {
        _isAuthenticated.value = authenticated
        if (authenticated) {
            _requiresAuth.value = false
        }
    }
    
    /**
     * Mark that re-authentication is required (app went to background)
     */
    fun setRequiresAuth(required: Boolean) {
        _requiresAuth.value = required
    }
    
    /**
     * Called when app goes to background
     */
    fun onAppBackgrounded() {
        if (_isAuthenticated.value) {
            _requiresAuth.value = true
            _isAuthenticated.value = false
        }
    }
    
    /**
     * Called when app comes to foreground
     */
    fun onAppForegrounded() {
        // requiresAuth will be true if user was authenticated before
        // This will trigger the auth screen to show
    }
    
    /**
     * Reset all auth state (for logout or similar)
     */
    fun reset() {
        _isAuthenticated.value = false
        _requiresAuth.value = false
    }
}
