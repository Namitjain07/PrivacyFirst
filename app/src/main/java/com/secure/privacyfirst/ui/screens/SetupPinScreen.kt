package com.secure.privacyfirst.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.secure.privacyfirst.viewmodel.PasswordViewModel
import kotlinx.coroutines.launch

@Composable
fun PinBox(
    value: String,
    onChange: (String) -> Unit,
    current: FocusRequester,
    next: FocusRequester?,
    previous: FocusRequester?
) {
    OutlinedTextField(
        value = value,
        onValueChange = { new ->
            val digit = new.take(1).filter { it.isDigit() }
            onChange(digit)
            
            if (digit.isNotEmpty()) {
                // Move forward when entering a digit
                next?.requestFocus()
            } else if (new.isEmpty() && value.isNotEmpty()) {
                // Backspace pressed - cleared current digit, move to previous
                previous?.requestFocus()
            }
        },
        singleLine = true,
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier
            .size(60.dp)
            .focusRequester(current),
        shape = RoundedCornerShape(10.dp),
        textStyle = LocalTextStyle.current.copy(
            fontSize = 20.sp,
            textAlign = TextAlign.Center
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupPinScreen(
    onBackClick: () -> Unit,
    onPinSet: () -> Unit,
    viewModel: PasswordViewModel = viewModel()
) {
    // Step 0: Verify old PIN (only if PIN is already set)
    var oldPin1 by remember { mutableStateOf("") }
    var oldPin2 by remember { mutableStateOf("") }
    var oldPin3 by remember { mutableStateOf("") }
    var oldPin4 by remember { mutableStateOf("") }
    
    // Step 1: Enter new PIN
    var pin1 by remember { mutableStateOf("") }
    var pin2 by remember { mutableStateOf("") }
    var pin3 by remember { mutableStateOf("") }
    var pin4 by remember { mutableStateOf("") }
    
    // Step 2: Confirm new PIN
    var confirmPin1 by remember { mutableStateOf("") }
    var confirmPin2 by remember { mutableStateOf("") }
    var confirmPin3 by remember { mutableStateOf("") }
    var confirmPin4 by remember { mutableStateOf("") }
    
    var currentStep by remember { mutableStateOf(0) } // 0 = verify old, 1 = enter new, 2 = confirm
    
    val or1 = remember { FocusRequester() }
    val or2 = remember { FocusRequester() }
    val or3 = remember { FocusRequester() }
    val or4 = remember { FocusRequester() }
    val r1 = remember { FocusRequester() }
    val r2 = remember { FocusRequester() }
    val r3 = remember { FocusRequester() }
    val r4 = remember { FocusRequester() }
    val cr1 = remember { FocusRequester() }
    val cr2 = remember { FocusRequester() }
    val cr3 = remember { FocusRequester() }
    val cr4 = remember { FocusRequester() }
    
    var error by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val isPinSet by viewModel.isPinSet.collectAsState()
    
    val oldPin = oldPin1 + oldPin2 + oldPin3 + oldPin4
    val pin = pin1 + pin2 + pin3 + pin4
    val confirmPin = confirmPin1 + confirmPin2 + confirmPin3 + confirmPin4
    
    LaunchedEffect(isPinSet) {
        // Set initial step based on whether PIN is already set
        currentStep = if (isPinSet) 0 else 1 // Start at verify step if PIN exists, else new PIN
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isPinSet) "Change PIN" else "Setup PIN") },
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
                        text = when (currentStep) {
                            0 -> "Verify Current PIN"
                            1 -> if (isPinSet) "Create New PIN" else "Create Your PIN"
                            else -> "Confirm New PIN"
                        },
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    Text(
                        text = when (currentStep) {
                            0 -> "Enter your current PIN to proceed"
                            1 -> "Enter a 4-digit PIN to secure your passwords"
                            else -> "Re-enter your PIN to confirm"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Step indicator
                    if (isPinSet) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            repeat(3) { index ->
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .padding(2.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Surface(
                                        modifier = Modifier.size(if (currentStep == index) 8.dp else 6.dp),
                                        shape = RoundedCornerShape(50),
                                        color = if (currentStep >= index) 
                                            MaterialTheme.colorScheme.primary 
                                        else 
                                            MaterialTheme.colorScheme.surfaceVariant
                                    ) {}
                                }
                                if (index < 2) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    
                    when (currentStep) {
                        0 -> {
                            // Verify old PIN
                            Text(
                                text = "Current PIN",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                PinBox(oldPin1, { oldPin1 = it; if (error.isNotEmpty()) error = "" }, or1, or2, null)
                                PinBox(oldPin2, { oldPin2 = it; if (error.isNotEmpty()) error = "" }, or2, or3, or1)
                                PinBox(oldPin3, { oldPin3 = it; if (error.isNotEmpty()) error = "" }, or3, or4, or2)
                                PinBox(oldPin4, { oldPin4 = it; if (error.isNotEmpty()) error = "" }, or4, null, or3)
                            }
                        }
                        1 -> {
                            // Enter new PIN
                            Text(
                                text = if (isPinSet) "New PIN" else "Enter PIN",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                PinBox(pin1, { pin1 = it; if (error.isNotEmpty()) error = "" }, r1, r2, null)
                                PinBox(pin2, { pin2 = it; if (error.isNotEmpty()) error = "" }, r2, r3, r1)
                                PinBox(pin3, { pin3 = it; if (error.isNotEmpty()) error = "" }, r3, r4, r2)
                                PinBox(pin4, { pin4 = it; if (error.isNotEmpty()) error = "" }, r4, null, r3)
                            }
                        }
                        2 -> {
                            // Confirm PIN
                            Text(
                                text = "Confirm PIN",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                PinBox(confirmPin1, { confirmPin1 = it; if (error.isNotEmpty()) error = "" }, cr1, cr2, null)
                                PinBox(confirmPin2, { confirmPin2 = it; if (error.isNotEmpty()) error = "" }, cr2, cr3, cr1)
                                PinBox(confirmPin3, { confirmPin3 = it; if (error.isNotEmpty()) error = "" }, cr3, cr4, cr2)
                                PinBox(confirmPin4, { confirmPin4 = it; if (error.isNotEmpty()) error = "" }, cr4, null, cr3)
                            }
                        }
                    }
                    
                    if (error.isNotEmpty()) {
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Button(
                        onClick = {
                            when (currentStep) {
                                0 -> {
                                    // Verify old PIN
                                    if (oldPin.length != 4) {
                                        error = "PIN must be 4 digits"
                                    } else {
                                        isLoading = true
                                        scope.launch {
                                            val isValid = viewModel.verifyPin(oldPin)
                                            isLoading = false
                                            if (isValid) {
                                                currentStep = 1
                                                error = ""
                                            } else {
                                                error = "Incorrect PIN. Please try again."
                                                oldPin1 = ""
                                                oldPin2 = ""
                                                oldPin3 = ""
                                                oldPin4 = ""
                                                or1.requestFocus()
                                            }
                                        }
                                    }
                                }
                                1 -> {
                                    // Enter new PIN
                                    if (pin.length != 4) {
                                        error = "PIN must be 4 digits"
                                    } else {
                                        currentStep = 2
                                        error = ""
                                    }
                                }
                                2 -> {
                                    // Confirm PIN
                                    when {
                                        confirmPin.length != 4 -> {
                                            error = "Please confirm your PIN"
                                        }
                                        pin != confirmPin -> {
                                            error = "PINs don't match"
                                            confirmPin1 = ""
                                            confirmPin2 = ""
                                            confirmPin3 = ""
                                            confirmPin4 = ""
                                            cr1.requestFocus()
                                        }
                                        else -> {
                                            isLoading = true
                                            scope.launch {
                                                viewModel.savePin(pin)
                                                isLoading = false
                                                onPinSet()
                                            }
                                        }
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !isLoading && when (currentStep) {
                            0 -> oldPin.length == 4
                            1 -> pin.length == 4
                            2 -> confirmPin.length == 4
                            else -> false
                        }
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text(
                                when (currentStep) {
                                    0 -> "Verify PIN"
                                    1 -> "Continue"
                                    2 -> if (isPinSet) "Update PIN" else "Set PIN"
                                    else -> "Continue"
                                }
                            )
                        }
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
                        text = "Remember your PIN. It's required to access your saved passwords.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}
