package com.secure.privacyfirst

import android.os.Bundle
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.secure.privacyfirst.auth.AuthStateManager
import com.secure.privacyfirst.navigation.AppNavigation
import com.secure.privacyfirst.ui.theme.PrivacyFirstTheme

class MainActivity : AppCompatActivity() {
    
    private val appLifecycleObserver = object : DefaultLifecycleObserver {
        override fun onStop(owner: LifecycleOwner) {
            super.onStop(owner)
            // App went to background - require re-authentication
            AuthStateManager.onAppBackgrounded()
        }
        
        override fun onStart(owner: LifecycleOwner) {
            super.onStart(owner)
            // App came to foreground
            AuthStateManager.onAppForegrounded()
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Enable WebView debugging
        WebView.setWebContentsDebuggingEnabled(true)
        
        enableEdgeToEdge()
        
        // Edge-to-edge automatically handles system bar colors
        WindowCompat.setDecorFitsSystemWindows(window, false)
        
        // Register lifecycle observer to track app foreground/background state
        ProcessLifecycleOwner.get().lifecycle.addObserver(appLifecycleObserver)
        
        // Handle back button press using OnBackPressedDispatcher (modern approach)
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Custom back navigation logic can be added here
                // For default behavior, finish the activity or let the system handle it
                if (isEnabled) {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })
        
        setContent {
            PrivacyFirstTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        // Cleanup lifecycle observer
        ProcessLifecycleOwner.get().lifecycle.removeObserver(appLifecycleObserver)
    }
}