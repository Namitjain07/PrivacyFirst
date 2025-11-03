package com.secure.privacyfirst

import android.content.Intent
import android.os.Bundle
import android.webkit.WebView
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowCompat
import com.secure.privacyfirst.navigation.AppNavigation
import com.secure.privacyfirst.ui.theme.PrivacyFirstTheme

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable WebView debugging
        WebView.setWebContentsDebuggingEnabled(true)

        enableEdgeToEdge()

        // Make status bar transparent
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = Color.Transparent.toArgb()
        window.navigationBarColor = Color.Transparent.toArgb()

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

//        // ✅ Start CameraWarningActivity
//        val intent = Intent(this, CameraWarningActivity::class.java)
//        startActivity(intent)
    }

    override fun onBackPressed() {
        onBackPressedDispatcher.onBackPressed() // ✅ Fixed line
    }
}
