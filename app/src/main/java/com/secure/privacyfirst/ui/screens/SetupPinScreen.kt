package com.secure.privacyfirst.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.secure.privacyfirst.viewmodel.PasswordViewModel
import kotlinx.coroutines.launch

enum class PinSetupStep {
    VERIFY_OLD_PIN,
    ENTER_NEW_PIN,
    CONFIRM_NEW_PIN
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupPinScreen(
    onBackClick: () -> Unit,
    onPinSet: () -> Unit,
    viewModel: PasswordViewModel = viewModel()
) {
    var currentStep by remember { mutableStateOf(PinSetupStep.ENTER_NEW_PIN) }
    var oldPin by remember { mutableStateOf("") }
    var newPin by remember { mutableStateOf("") }
    var confirmPin by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val isPinSet by viewModel.isPinSet.collectAsState()
    
    // Determine initial step based on whether PIN is already set
    LaunchedEffect(isPinSet) {
        currentStep = if (isPinSet) {
            PinSetupStep.VERIFY_OLD_PIN
        } else {
            PinSetupStep.ENTER_NEW_PIN
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        when (currentStep) {
                            PinSetupStep.VERIFY_OLD_PIN -> "Verify Current PIN"
                            PinSetupStep.ENTER_NEW_PIN -> if (isPinSet) "Change PIN" else "Setup PIN"
                            PinSetupStep.CONFIRM_NEW_PIN -> "Confirm New PIN"
                        }
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            when (currentStep) {
                PinSetupStep.VERIFY_OLD_PIN -> {
                    VerifyOldPinContent(
                        oldPin = oldPin,
                        onOldPinChange = { 
                            oldPin = it
                            if (error.isNotEmpty()) error = ""
                        },
                        error = error,
                        isLoading = isLoading,
                        onVerify = {
                            if (oldPin.length < 4 || oldPin.length > 6) {
                                error = "PIN must be 4-6 digits"
                            } else {
                                isLoading = true
                                scope.launch {
                                    val isValid = viewModel.verifyPin(oldPin)
                                    isLoading = false
                                    if (isValid) {
                                        error = ""
                                        currentStep = PinSetupStep.ENTER_NEW_PIN
                                        oldPin = "" // Clear for security
                                    } else {
                                        error = "Incorrect PIN. Please try again."
                                        oldPin = ""
                                    }
                                }
                            }
                        }
                    )
                }
                
                PinSetupStep.ENTER_NEW_PIN -> {
                    EnterNewPinContent(
                        newPin = newPin,
                        onNewPinChange = { 
                            newPin = it
                            if (error.isNotEmpty()) error = ""
                        },
                        error = error,
                        isLoading = isLoading,
                        isPinSet = isPinSet,
                        onContinue = {
                            if (newPin.length < 4 || newPin.length > 6) {
                                error = "PIN must be 4-6 digits"
                            } else {
                                error = ""
                                currentStep = PinSetupStep.CONFIRM_NEW_PIN
                            }
                        }
                    )
                }
                
                PinSetupStep.CONFIRM_NEW_PIN -> {
                    ConfirmNewPinContent(
                        confirmPin = confirmPin,
                        onConfirmPinChange = { 
                            confirmPin = it
                            if (error.isNotEmpty()) error = ""
                        },
                        error = error,
                        isLoading = isLoading,
                        onConfirm = {
                            if (confirmPin.length < 4 || confirmPin.length > 6) {
                                error = "PIN must be 4-6 digits"
                            } else if (newPin != confirmPin) {
                                error = "PINs don't match. Please try again."
                                confirmPin = ""
                            } else {
                                isLoading = true
                                scope.launch {
                                    viewModel.savePin(newPin)
                                    isLoading = false
                                    // Clear all PINs for security
                                    newPin = ""
                                    confirmPin = ""
                                    onPinSet()
                                }
                            }
                        },
                        onBack = {
                            confirmPin = ""
                            error = ""
                            currentStep = PinSetupStep.ENTER_NEW_PIN
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun VerifyOldPinContent(
    oldPin: String,
    onOldPinChange: (String) -> Unit,
    error: String,
    isLoading: Boolean,
    onVerify: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(64.dp)
            )
            
            Text(
                text = "Verify Your Current PIN",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            
            Text(
                text = "For security, please enter your current PIN before setting a new one",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            OutlinedTextField(
                value = oldPin,
                onValueChange = { new ->
                    val filtered = new.filter { it.isDigit() }.take(6)
                    onOldPinChange(filtered)
                },
                label = { Text("Current PIN") },
                placeholder = { Text("••••") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                isError = error.isNotEmpty()
            )
            
            if (error.isNotEmpty()) {
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Button(
                onClick = onVerify,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = !isLoading && oldPin.length >= 4 && oldPin.length <= 6
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Verify PIN")
                }
            }
        }
    }
}

@Composable
private fun EnterNewPinContent(
    newPin: String,
    onNewPinChange: (String) -> Unit,
    error: String,
    isLoading: Boolean,
    isPinSet: Boolean,
    onContinue: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = if (isPinSet) "Enter New PIN" else "Create Your PIN",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            
            Text(
                text = "Enter a 4-6 digit PIN to secure your passwords",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            OutlinedTextField(
                value = newPin,
                onValueChange = { new ->
                    val filtered = new.filter { it.isDigit() }.take(6)
                    onNewPinChange(filtered)
                },
                label = { Text(if (isPinSet) "New PIN" else "Enter PIN") },
                placeholder = { Text("••••") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                isError = error.isNotEmpty()
            )
            
            if (error.isNotEmpty()) {
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Button(
                onClick = onContinue,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = !isLoading && newPin.length >= 4 && newPin.length <= 6
            ) {
                Text("Continue")
            }
        }
    }
    
    Spacer(modifier = Modifier.height(16.dp))
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "⚠️ Important",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "• Choose a PIN that's easy to remember but hard to guess\n• Don't use obvious numbers like 1234 or your birth year\n• Remember your PIN - it's required to access your passwords",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
private fun ConfirmNewPinContent(
    confirmPin: String,
    onConfirmPinChange: (String) -> Unit,
    error: String,
    isLoading: Boolean,
    onConfirm: () -> Unit,
    onBack: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Confirm Your PIN",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            
            Text(
                text = "Re-enter your PIN to confirm",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            OutlinedTextField(
                value = confirmPin,
                onValueChange = { new ->
                    val filtered = new.filter { it.isDigit() }.take(6)
                    onConfirmPinChange(filtered)
                },
                label = { Text("Confirm PIN") },
                placeholder = { Text("••••") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                isError = error.isNotEmpty()
            )
            
            if (error.isNotEmpty()) {
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isLoading
                ) {
                    Text("Back")
                }
                
                Button(
                    onClick = onConfirm,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isLoading && confirmPin.length >= 4 && confirmPin.length <= 6
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("Confirm")
                    }
                }
            }
        }
    }
    
    Spacer(modifier = Modifier.height(16.dp))
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = "Your PIN will be securely encrypted and stored",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}
