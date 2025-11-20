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
import android.util.Log
import com.secure.privacyfirst.data.CryptoUtils
import com.secure.privacyfirst.data.PinEntity
import com.secure.privacyfirst.data.UserPreferencesManager
import kotlinx.coroutines.launch

private const val TAG = "SetupScreen"

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
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(60.dp))
        
        // Header
        Text(
            text = "Complete Your Setup",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Please enter your name and create a secure PIN",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(48.dp))
        
        // Name Input
        OutlinedTextField(
            value = userName,
            onValueChange = { 
                userName = it
                showError = false
            },
            label = { Text("Your Name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)
            )
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // PIN Input
        OutlinedTextField(
            value = pin,
            onValueChange = { 
                if (it.length <= 6 && it.all { char -> char.isDigit() }) {
                    pin = it
                    showError = false
                }
            },
            label = { Text("Create 4-6 Digit PIN") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = if (isPinVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            trailingIcon = {
                IconButton(onClick = { isPinVisible = !isPinVisible }) {
                    Icon(
                        imageVector = if (isPinVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (isPinVisible) "Hide PIN" else "Show PIN"
                    )
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)
            )
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Confirm PIN Input
        OutlinedTextField(
            value = confirmPin,
            onValueChange = { 
                if (it.length <= 6 && it.all { char -> char.isDigit() }) {
                    confirmPin = it
                    showError = false
                }
            },
            label = { Text("Confirm PIN") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = if (isConfirmPinVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            trailingIcon = {
                IconButton(onClick = { isConfirmPinVisible = !isConfirmPinVisible }) {
                    Icon(
                        imageVector = if (isConfirmPinVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (isConfirmPinVisible) "Hide PIN" else "Show PIN"
                    )
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)
            ),
            isError = showError
        )
        
        if (showError) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Setup Button
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
                                // Hash and save PIN
                                val hashedPin = CryptoUtils.hashPin(pin)
                                pinDao.insertPin(
                                    PinEntity(
                                        encryptedPin = hashedPin
                                    )
                                )
                                
                                // Save setup completion and user name
                                Log.d(TAG, "Saving userName: '$userName'")
                                preferencesManager.setSetupCompleted(userName)
                                
                                Toast.makeText(
                                    context,
                                    "Setup completed successfully!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                
                                onSetupComplete()
                            } catch (e: Exception) {
                                showError = true
                                errorMessage = "Failed to save setup: ${e.message}"
                            }
                        }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            enabled = userName.isNotBlank() && pin.length >= 4 && confirmPin.length >= 4
        ) {
            Text(
                text = "Complete Setup",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Info text
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            )
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "💡",
                    fontSize = 20.sp,
                    modifier = Modifier.padding(end = 12.dp)
                )
                Column {
                    Text(
                        text = "Your PIN protects your passwords",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Make sure to remember it - there's no way to recover a forgotten PIN.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}
