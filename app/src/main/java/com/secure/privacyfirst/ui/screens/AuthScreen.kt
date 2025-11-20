package com.secure.privacyfirst.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.secure.privacyfirst.navigation.Screen
import com.secure.privacyfirst.viewmodel.PasswordViewModel
import kotlinx.coroutines.launch
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.font.FontWeight
@Composable
fun AuthScreen(
    navController: NavHostController? = null,
    onAuthenticated: () -> Unit = {},
    viewModel: PasswordViewModel = viewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    val isPinSet by viewModel.isPinSet.collectAsState()

    var d1 by rememberSaveable { mutableStateOf("") }
    var d2 by rememberSaveable { mutableStateOf("") }
    var d3 by rememberSaveable { mutableStateOf("") }
    var d4 by rememberSaveable { mutableStateOf("") }

    var error by remember { mutableStateOf("") }

    val r1 = remember { FocusRequester() }
    val r2 = remember { FocusRequester() }
    val r3 = remember { FocusRequester() }
    val r4 = remember { FocusRequester() }

    val pin = d1 + d2 + d3 + d4
    val submitEnabled = pin.length == 4
    fun startBiometricAuth() {
        val activity = context as? FragmentActivity ?: return
        val executor = ContextCompat.getMainExecutor(context)

        val biometricManager = BiometricManager.from(context)
        if (biometricManager.canAuthenticate(Authenticators.BIOMETRIC_STRONG)
            != BiometricManager.BIOMETRIC_SUCCESS
        ) {
            Toast.makeText(context, "Biometric not available", Toast.LENGTH_SHORT).show()
            return
        }
        val prompt = BiometricPrompt(
            activity,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    Toast.makeText(context, "Authenticated", Toast.LENGTH_SHORT).show()
                    if (navController != null) {
                        navController.navigate(Screen.WebView.route) {
                            popUpTo(Screen.Auth.route) { inclusive = true }
                        }
                    } else onAuthenticated()
                }

                override fun onAuthenticationFailed() {
                    Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
                }

                override fun onAuthenticationError(code: Int, err: CharSequence) {
                    Toast.makeText(context, err, Toast.LENGTH_SHORT).show()
                }
            }
        )

        val info = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Authenticate")
            .setSubtitle("Use fingerprint or face to continue")
            .setNegativeButtonText("Use PIN")
            .build()

        prompt.authenticate(info)
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(36.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {

            Image(
                painter = painterResource(id = com.secure.privacyfirst.R.drawable.logo),
                contentDescription = "App Logo",
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "PRIVACY FIRST BROWSER",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Enter Security Pin",
            fontSize = 26.sp,
            color = Color.Black,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Enter the PIN set during setup",
            fontSize = 13.sp,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(40.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {

            @Composable
            fun PinDigit(
                value: String,
                onChange: (String) -> Unit,
                current: FocusRequester,
                next: FocusRequester?
            ) {
                OutlinedTextField(
                    value = value,
                    onValueChange = { new ->
                        val digit = new.take(1).filter { it.isDigit() }
                        onChange(digit)
                        if (digit.isNotEmpty()) next?.requestFocus()
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

            PinDigit(d1, { d1 = it }, r1, r2)
            PinDigit(d2, { d2 = it }, r2, r3)
            PinDigit(d3, { d3 = it }, r3, r4)
            PinDigit(d4, { d4 = it }, r4, null)
        }
        Spacer(modifier = Modifier.height(20.dp))
        if (error.isNotEmpty()) {
            Text(error, color = Color.Red)
            Spacer(modifier = Modifier.height(12.dp))
        }
        Button(
            onClick = {
                if (pin.length != 4) {
                    error = "Enter a 4-digit PIN"
                    return@Button
                }
                scope.launch {
                    val valid = if (!isPinSet) true else viewModel.verifyPin(pin)

                    if (valid) {
                        Toast.makeText(context, "PIN Accepted", Toast.LENGTH_SHORT).show()
                        if (navController != null) {
                            navController.navigate(Screen.WebView.route) {
                                popUpTo(Screen.Auth.route) { inclusive = true }
                            }
                        } else onAuthenticated()
                    } else {
                        error = "Invalid PIN"
                        d1 = ""; d2 = ""; d3 = ""; d4 = ""
                        r1.requestFocus()
                    }
                }
            },
            enabled = submitEnabled,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4CAF50),
                contentColor = Color.White,
                disabledContainerColor = Color(0xFFDCDCDC),
                disabledContentColor = Color.DarkGray
            )
        ) {
            Text("SUBMIT", color = Color.DarkGray)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text("OR", color = Color.Gray)
        Spacer(modifier = Modifier.height(12.dp))
        TextButton(onClick = { startBiometricAuth() }) {
            Text("Verify using your fingerprint")
        }
    }
    LaunchedEffect(Unit) {
        r1.requestFocus()
    }
}
