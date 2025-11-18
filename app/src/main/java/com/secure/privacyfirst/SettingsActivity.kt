package com.secure.privacyfirst

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.secure.privacyfirst.ui.theme.PrivacyFirstTheme

class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Make status bar transparent
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = Color.Transparent.toArgb()
        window.navigationBarColor = Color.Transparent.toArgb()

        setContent {
            PrivacyFirstTheme {
                SettingsScreen(onBackClick = { finish() })
            }
        }
    }
}

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
        }
    }
}

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onBackClick: () -> Unit) {
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
        ) {
            // remember selected level state
            var selectedLevel by remember { mutableStateOf(SecurityLevel.LOW) }

            SecurityLevelSelector(
                selectedLevel = selectedLevel,
                onLevelChange = { selectedLevel = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Other cards (your previous content)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "🔒 HTTPS Only",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Only secure HTTPS connections are allowed. HTTP traffic is blocked.",
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
                        text = "🛡️ SSL Certificate Verification",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Connections verified with SSL certificates. Trusted banking sites are allowed to proceed.",
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
                    )
                }
            }
        }
    }
}
