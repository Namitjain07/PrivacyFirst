package com.secure.privacyfirst.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.secure.privacyfirst.data.CryptoUtils
import com.secure.privacyfirst.data.PinEntity
import com.secure.privacyfirst.data.UserPreferencesManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupScreen(
    onSetupComplete: () -> Unit,
    pinDao: com.secure.privacyfirst.data.PinDao
) {
    var userName by remember { mutableStateOf("") }
    var pin by remember { mutableStateOf("") }
    var confirmPin by remember { mutableStateOf("") }
    var isPinVisible by remember { mutableStateOf(false) }
    var isConfirmPinVisible by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val preferencesManager = remember { UserPreferencesManager(context) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(50.dp))

        // ---------- HEADER (clean, simple) ----------
        Text(
            text = "Complete Your Setup",
            modifier = Modifier.padding(top = 32.dp),
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Enter your name and create a secure PIN",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(40.dp))

        // ---------- INPUT FIELDS ----------
        Column(
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            // Name
            ModernInputField(
                value = userName,
                onChange = {
                    userName = it
                    showError = false
                },
                label = "Your Name"
            )

            // PIN
            ModernPinField(
                value = pin,
                onChange = {
                    if (it.length <= 6 && it.all { c -> c.isDigit() }) {
                        pin = it
                        showError = false
                    }
                },
                label = "Create 4–6 Digit PIN",
                visible = isPinVisible,
                changeVisibility = { isPinVisible = !isPinVisible }
            )

            // Confirm PIN
            ModernPinField(
                value = confirmPin,
                onChange = {
                    if (it.length <= 6 && it.all { c -> c.isDigit() }) {
                        confirmPin = it
                        showError = false
                    }
                },
                label = "Confirm PIN",
                visible = isConfirmPinVisible,
                changeVisibility = { isConfirmPinVisible = !isConfirmPinVisible },
                isError = showError
            )
        }

        if (showError) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // ---------- BUTTON ----------
        Button(
            onClick = {
                when {
                    userName.isBlank() -> {
                        showError = true
                        errorMessage = "Please enter your name"
                    }
                    pin.length < 4 -> {
                        showError = true
                        errorMessage = "PIN must be at least 4 digits"
                    }
                    pin != confirmPin -> {
                        showError = true
                        errorMessage = "PINs do not match"
                    }
                    else -> {
                        scope.launch {
                            try {
                                val hashedPin = CryptoUtils.hashPin(pin)
                                pinDao.insertPin(
                                    PinEntity(
                                        encryptedPin = hashedPin
                                    )
                                )
                                preferencesManager.setSetupCompleted(userName)

                                Toast.makeText(
                                    context,
                                    "Setup complete!",
                                    Toast.LENGTH_SHORT
                                ).show()

                                onSetupComplete()
                            } catch (e: Exception) {
                                showError = true
                                errorMessage = "Failed to save: ${e.message}"
                            }
                        }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(14.dp),
            enabled = userName.isNotBlank() && pin.length >= 4 && confirmPin.length >= 4
        ) {
            Text(
                text = "Complete Setup",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ---------- INFO CARD ----------
        InfoCardUI()
    }
}

/* ----------------------------------
     REUSABLE UI COMPONENTS (NO GRADIENTS)
   ---------------------------------- */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernInputField(
    value: String,
    onChange: (String) -> Unit,
    label: String
) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label) },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp)
        ,
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
            focusedBorderColor = MaterialTheme.colorScheme.primary
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernPinField(
    value: String,
    onChange: (String) -> Unit,
    label: String,
    visible: Boolean,
    changeVisibility: () -> Unit,
    isError: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label) },
        singleLine = true,
        isError = isError,
        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
        trailingIcon = {
            IconButton(onClick = changeVisibility) {
                Icon(
                    imageVector = if (visible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                    contentDescription = null
                )
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp)),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
            focusedBorderColor = MaterialTheme.colorScheme.primary
        )
    )
}

@Composable
fun InfoCardUI() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("💡", fontSize = 22.sp, modifier = Modifier.padding(end = 12.dp))

            Column {
                Text(
                    "Your PIN protects your passwords",
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Remember it — it cannot be recovered.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
