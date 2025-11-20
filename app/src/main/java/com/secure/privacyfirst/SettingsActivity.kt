package com.secure.privacyfirst

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
<<<<<<< HEAD
import androidx.compose.foundation.BorderStroke
=======
import androidx.compose.foundation.clickable
>>>>>>> 96dfbad6bc32369dcd78005432156bf4934c1608
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
<<<<<<< HEAD
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
=======
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
>>>>>>> 96dfbad6bc32369dcd78005432156bf4934c1608
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.secure.privacyfirst.data.SecurityLevel
import com.secure.privacyfirst.data.UserPreferencesManager
import com.secure.privacyfirst.ui.screens.PasswordManagerScreen
import com.secure.privacyfirst.ui.screens.SetupPinScreen
import com.secure.privacyfirst.ui.theme.PrivacyFirstTheme
import kotlinx.coroutines.launch

class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
<<<<<<< HEAD

        // Make status bar transparent
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = Color.Transparent.toArgb()
        window.navigationBarColor = Color.Transparent.toArgb()

=======
        
        // Edge-to-edge automatically handles system bar colors
        WindowCompat.setDecorFitsSystemWindows(window, false)
        
>>>>>>> 96dfbad6bc32369dcd78005432156bf4934c1608
        setContent {
            PrivacyFirstTheme {
                SettingsNavigation(onBackClick = { finish() })
            }
        }
    }
}

<<<<<<< HEAD
enum class SecurityLevel { LOW, MEDIUM, HIGH }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecurityLevelSelector(
    selectedLevel: SecurityLevel,
    onLevelChange: (SecurityLevel) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Header: shield icon text + info icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row {
                    Text("🛡️", modifier = Modifier.padding(end = 8.dp))
                    Text(
                        "Security Level",
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                IconButton(onClick = { /* show info dialog if needed */ }) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Info"
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Segmented control row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SegmentedButton(
                    text = "Low",
                    selected = selectedLevel == SecurityLevel.LOW,
                    onClick = { onLevelChange(SecurityLevel.LOW) },
                    modifier = Modifier.weight(1f)
                )
                SegmentedButton(
                    text = "Medium",
                    selected = selectedLevel == SecurityLevel.MEDIUM,
                    onClick = { onLevelChange(SecurityLevel.MEDIUM) },
                    modifier = Modifier.weight(1f)
                )
                SegmentedButton(
                    text = "High",
                    selected = selectedLevel == SecurityLevel.HIGH,
                    onClick = { onLevelChange(SecurityLevel.HIGH) },
                    modifier = Modifier.weight(1f)
                )
            }
=======
@Composable
fun SettingsNavigation(onBackClick: () -> Unit) {
    val navController = rememberNavController()
    val context = LocalContext.current
    
    NavHost(
        navController = navController,
        startDestination = "settings_main"
    ) {
        composable("settings_main") {
            SettingsScreen(
                onBackClick = onBackClick,
                onNavigateToPasswordManager = {
                    navController.navigate("password_manager")
                },
                onNavigateToSetupPin = {
                    navController.navigate("setup_pin")
                },
                onNavigateToTerms = {
                    context.startActivity(Intent(context, TermsActivity::class.java))
                }
            )
        }
        
        composable("password_manager") {
            PasswordManagerScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        
        composable("setup_pin") {
            SetupPinScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onPinSet = {
                    navController.popBackStack()
                }
            )
>>>>>>> 96dfbad6bc32369dcd78005432156bf4934c1608
        }
    }
}

<<<<<<< HEAD
@Composable
fun SegmentedButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f) else Color.Transparent
    val contentColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
    val borderColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline

    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .height(44.dp),
        shape = MaterialTheme.shapes.small,
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        border = BorderStroke(width = if (selected) 2.dp else 1.dp, color = borderColor)
    ) {
        Text(text)
    }
}

=======
>>>>>>> 96dfbad6bc32369dcd78005432156bf4934c1608
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    onNavigateToPasswordManager: () -> Unit = {},
    onNavigateToSetupPin: () -> Unit = {},
    onNavigateToTerms: () -> Unit = {}
) {
    val context = LocalContext.current
    val preferencesManager = remember { UserPreferencesManager(context) }
    val currentSecurityLevel by preferencesManager.securityLevel.collectAsState(initial = SecurityLevel.MEDIUM)
    val coroutineScope = rememberCoroutineScope()
    var showSecurityInfoDialog by remember { mutableStateOf(false) }
    
    Scaffold(
        modifier = Modifier.statusBarsPadding(),
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
<<<<<<< HEAD
            // remember selected level state
            var selectedLevel by remember { mutableStateOf(SecurityLevel.LOW) }

            SecurityLevelSelector(
                selectedLevel = selectedLevel,
                onLevelChange = { selectedLevel = it }
=======
            // Security Level Section
            Text(
                text = "Security Level",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp)
>>>>>>> 96dfbad6bc32369dcd78005432156bf4934c1608
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Other cards (your previous content)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🔐 Security Settings",
                            style = MaterialTheme.typography.titleMedium
                        )
                        IconButton(onClick = { showSecurityInfoDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Security Level Info",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Current Level: ${currentSecurityLevel.getDisplayName()}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    
                    Text(
                        text = currentSecurityLevel.getDescription(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Security Level Selector
                    Column(modifier = Modifier.fillMaxWidth()) {
                        SecurityLevel.entries.forEach { level ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = currentSecurityLevel == level,
                                    onClick = {
                                        coroutineScope.launch {
                                            preferencesManager.setSecurityLevel(level)
                                        }
                                    }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = level.getDisplayName(),
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    Text(
                                        text = level.getDescription(),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Password Management",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clickable { onNavigateToPasswordManager() }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "🔐 Password Manager",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Manage your saved passwords securely",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Go to Password Manager",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clickable { onNavigateToSetupPin() }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "🔢 Setup PIN",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Create or change your security PIN",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Go to Setup PIN",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }
<<<<<<< HEAD

=======
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Privacy & Security Settings",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
>>>>>>> 96dfbad6bc32369dcd78005432156bf4934c1608
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clickable { onNavigateToTerms() }
            ) {
<<<<<<< HEAD
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "🏦 Trusted Banking Sites Only",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Access restricted to verified banking domains: SBI, ICICI, Kotak, YES Bank, Citi, AMEX, UCO, IndusInd.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "🧹 Clear Browsing Data",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Automatically cleared after each session. No cookies, no history, no traces.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "🔐 Enhanced Privacy",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Geolocation disabled, form data not saved, mixed content blocked.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
=======
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "📜 Terms and Conditions",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Read the terms and conditions",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Go to Terms and Conditions",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
>>>>>>> 96dfbad6bc32369dcd78005432156bf4934c1608
                    )
                }
            }
        }
    }
    
    // Security Info Dialog
    if (showSecurityInfoDialog) {
        AlertDialog(
            onDismissRequest = { showSecurityInfoDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Info"
                )
            },
            title = {
                Text(text = "Security Levels Explained")
            },
            text = {
                Column {
                    Text(
                        text = "Choose the security level that fits your needs:",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    SecurityLevel.entries.forEach { level ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = level.getDisplayName(),
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = level.getDetailedDescription(),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showSecurityInfoDialog = false }) {
                    Text("Got it")
                }
            }
        )
    }
}
