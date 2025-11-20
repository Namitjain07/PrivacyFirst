package com.secure.privacyfirst.data

import android.app.Activity
import android.app.Application
import android.os.Bundle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppLockManager private constructor(private val application: Application) {
    
    private val _isAppLocked = MutableStateFlow(false)
    val isAppLocked: StateFlow<Boolean> = _isAppLocked.asStateFlow()
    
    private var isPinSet = false
    private var activityReferences = 0
    private var isChangingConfigurations = false
    
    init {
        registerActivityLifecycleCallbacks()
    }
    
    private fun registerActivityLifecycleCallbacks() {
        application.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
            
            override fun onActivityStarted(activity: Activity) {
                if (activityReferences == 0 && !isChangingConfigurations && isPinSet) {
                    // App came to foreground from background - lock immediately
                    _isAppLocked.value = true
                }
                activityReferences++
                isChangingConfigurations = false
            }
            
            override fun onActivityResumed(activity: Activity) {}
            
            override fun onActivityPaused(activity: Activity) {}
            
            override fun onActivityStopped(activity: Activity) {
                activityReferences--
                isChangingConfigurations = activity.isChangingConfigurations
            }
            
            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
            
            override fun onActivityDestroyed(activity: Activity) {}
        })
    }
    
    fun setPinConfigured(configured: Boolean) {
        isPinSet = configured
    }
    
    fun unlock() {
        _isAppLocked.value = false
    }
    
    fun lock() {
        if (isPinSet) {
            _isAppLocked.value = true
        }
    }
    
    companion object {
        @Volatile
        private var instance: AppLockManager? = null
        
        fun getInstance(application: Application): AppLockManager {
            return instance ?: synchronized(this) {
                instance ?: AppLockManager(application).also { instance = it }
            }
        }
    }
}
