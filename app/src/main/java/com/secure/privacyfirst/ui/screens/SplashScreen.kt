package com.secure.privacyfirst.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.secure.privacyfirst.R
import com.secure.privacyfirst.data.UserPreferencesManager
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNavigateToOnboarding: () -> Unit,
    onNavigateToSetup: () -> Unit,
    onNavigateToAuth: () -> Unit
) {
    val context = LocalContext.current
    val preferencesManager = UserPreferencesManager(context)
    
    val isOnboardingCompleted by preferencesManager.isOnboardingCompleted.collectAsState(initial = null)
    val isSetupCompleted by preferencesManager.isSetupCompleted.collectAsState(initial = null)
    
    LaunchedEffect(isOnboardingCompleted, isSetupCompleted) {
        // Wait until both values are loaded
        val onboardingDone = isOnboardingCompleted
        val setupDone = isSetupCompleted
        
        if (onboardingDone == null || setupDone == null) {
            return@LaunchedEffect
        }
        
        delay(2500)
        
        when {
            !onboardingDone -> onNavigateToOnboarding()
            !setupDone -> onNavigateToSetup()
            else -> onNavigateToAuth()
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Privacy First Browser Logo",
                modifier = Modifier.size(120.dp)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "PRIVACY FIRST",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                ),
                color = MaterialTheme.colorScheme.primary
            )
            
            Text(
                text = "BROWSER",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Normal,
                    letterSpacing = 2.sp
                ),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
        }
    }
}
