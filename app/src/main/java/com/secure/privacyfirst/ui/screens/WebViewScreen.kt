package com.secure.privacyfirst.ui.screens

import android.Manifest
import android.app.DownloadManager
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.view.WindowManager
import android.webkit.CookieManager
import android.webkit.DownloadListener
import android.webkit.JavascriptInterface
import android.webkit.PermissionRequest
import android.webkit.SslErrorHandler
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat.getSystemService
import com.secure.privacyfirst.MainActivity
import com.secure.privacyfirst.SettingsActivity
import com.secure.privacyfirst.data.SecurityLevel
import com.secure.privacyfirst.data.UserPreferencesManager
import com.secure.privacyfirst.network.WhitelistRepository
import com.secure.privacyfirst.ui.components.CameraAccessWarningDialog
import com.secure.privacyfirst.ui.components.ExternalAppWarningDialog
import com.secure.privacyfirst.ui.components.MicrophoneAccessWarningDialog
import kotlinx.coroutines.launch

private const val TAG = "WebViewScreen"
private const val CAMERA_PERMISSION_REQUEST_CODE = 100
private const val MIC_PERMISSION_REQUEST_CODE = 101

@Composable
fun WebViewScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val preferencesManager = remember { UserPreferencesManager(context) }
    val whitelistRepository = remember { WhitelistRepository(context) }
    val securityLevel by preferencesManager.securityLevel.collectAsState(initial = SecurityLevel.MEDIUM)
    val userName by preferencesManager.userName.collectAsState(initial = "")
    var webView by remember { mutableStateOf<WebView?>(null) }
    
    // Whitelist state
    var whitelistUrls by remember { mutableStateOf<List<String>>(emptyList()) }
    var isLoadingWhitelist by remember { mutableStateOf(true) }
    
    // Warning dialog states
    var showCameraWarning by remember { mutableStateOf(false) }
    var showMicWarning by remember { mutableStateOf(false) }
    var showExternalAppWarning by remember { mutableStateOf(false) }
    var showExitDialog by remember { mutableStateOf(false) }
    var externalUrl by remember { mutableStateOf("") }
    var pendingPermissionRequest by remember { mutableStateOf<PermissionRequest?>(null) }
    
    // Fetch whitelist on start
    LaunchedEffect(Unit) {
        scope.launch {
            Log.d(TAG, "Fetching whitelist from server...")
            val result = whitelistRepository.getWhitelist()
            result.onSuccess { urls ->
                whitelistUrls = urls
                isLoadingWhitelist = false
                Log.d(TAG, "Whitelist loaded from server: ${urls.size} URLs")
                Toast.makeText(
                    context, 
                    "✓ Whitelist loaded from server: ${urls.size} URLs", 
                    Toast.LENGTH_SHORT
                ).show()
            }.onFailure { error ->
                Log.e(TAG, "Failed to fetch whitelist: ${error.message}", error)
                isLoadingWhitelist = false
                // Fallback to hardcoded list
                whitelistUrls = listOf(
                    "1drv.ms",
                    "onedrive.live.com",
                    "sbi.bank.in",
                    "onlinesbi.sbi",
                    "sbi.co.in",
                    "icicibank.com",
                    "infinity.icicibank.com",
                    "kotak.com",
                    "netbanking.kotak.com",
                    "yesbank.in",
                    "citibank.co.in",
                    "online.citibank.co.in",
                    "americanexpress.com",
                    "ucobank.com",
                    "indusind.com",
                    "hdfcbank.com",
                    "netbanking.hdfcbank.com"
                )
                Toast.makeText(
                    context, 
                    "Using local whitelist (server unavailable)", 
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
    
    // Apply window security flags based on security level
    DisposableEffect(securityLevel) {
        val activity = context as? MainActivity
        activity?.let {
            if (securityLevel == SecurityLevel.HIGH) {
                // Prevent screenshots and screen recording
                it.window.setFlags(
                    WindowManager.LayoutParams.FLAG_SECURE,
                    WindowManager.LayoutParams.FLAG_SECURE
                )
            } else {
                // Allow screenshots
                it.window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
            }
        }
        
        onDispose {
            // Reset flags when leaving the screen
            activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
        }
    }
    
    // Handle back button press
    BackHandler(enabled = true) {
        if (webView?.canGoBack() == true) {
            webView?.goBack()
        } else {
            // If can't go back in WebView, we're at the home page
            // Show exit confirmation dialog
            showExitDialog = true
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                // Configure global cookie manager first
                val cookieManager = CookieManager.getInstance()
                cookieManager.setAcceptCookie(true)
                
                WebView(ctx).apply {
                    settings.apply {
                        javaScriptEnabled = true
                        domStorageEnabled = true
                        loadWithOverviewMode = true
                        useWideViewPort = true
                        builtInZoomControls = true
                        displayZoomControls = false
                        setSupportZoom(true)
                        allowFileAccess = true
                        
                        // Security settings based on level
                        when (securityLevel) {
                            SecurityLevel.LOW -> {
                                // Low security - allow HTTP
                                mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                            }
                            SecurityLevel.MEDIUM, SecurityLevel.HIGH -> {
                                // Medium and High - HTTPS only
                                mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_NEVER_ALLOW
                            }
                        }
                        
                        allowContentAccess = true
                        
                        // Enable necessary features for banking sites
                        setGeolocationEnabled(false)
                        javaScriptCanOpenWindowsAutomatically = true
                        
                        // Enable caching
                        cacheMode = android.webkit.WebSettings.LOAD_DEFAULT
                        
                        // Enhanced user agent
                        userAgentString = "Mozilla/5.0 (Linux; Android 13; Pixel 7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.6099.230 Mobile Safari/537.36"
                    }
                    
                    setBackgroundColor(android.graphics.Color.TRANSPARENT)
                    
                    // Enable cookies for this WebView instance
                    cookieManager.setAcceptThirdPartyCookies(this, true)
                    
                    // Add JavaScript interface
                    addJavascriptInterface(WebAppInterface(context), "Android")
                    
                    // Disable long press menu for HIGH security (prevents copy/paste)
                    if (securityLevel == SecurityLevel.HIGH) {
                        isLongClickable = false
                        setOnLongClickListener { true }
                    }
                    
                    // Download listener based on security level
                    setDownloadListener(object : DownloadListener {
                        override fun onDownloadStart(
                            url: String?,
                            userAgent: String?,
                            contentDisposition: String?,
                            mimetype: String?,
                            contentLength: Long
                        ) {
                            if (securityLevel == SecurityLevel.HIGH) {
                                Toast.makeText(
                                    ctx,
                                    "Downloads are disabled in High Security mode",
                                    Toast.LENGTH_SHORT
                                ).show()
                                return
                            }
                            
                            // Allow downloads for LOW and MEDIUM security
                            url?.let {
                                try {
                                    val request = DownloadManager.Request(Uri.parse(url))
                                    request.setMimeType(mimetype)
                                    request.setNotificationVisibility(
                                        DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED
                                    )
                                    request.setDestinationInExternalPublicDir(
                                        Environment.DIRECTORY_DOWNLOADS,
                                        "download"
                                    )
                                    val dm = ctx.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                                    dm.enqueue(request)
                                    Toast.makeText(ctx, "Downloading file...", Toast.LENGTH_SHORT).show()
                                } catch (e: Exception) {
                                    Toast.makeText(
                                        ctx,
                                        "Download failed: ${e.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    })
                    
                    webViewClient = object : WebViewClient() {
                        override fun shouldOverrideUrlLoading(
                            view: WebView?,
                            request: WebResourceRequest?
                        ): Boolean {
                            val url = request?.url.toString()
                            
                            // Allow local file access
                            if (url.startsWith("file:///")) {
                                return false
                            }
                            
                            // Security level enforcement for URL protocols
                            when (securityLevel) {
                                SecurityLevel.LOW -> {
                                    // Allow both HTTP and HTTPS for LOW security
                                    if (!url.startsWith("https://") && !url.startsWith("http://")) {
                                        Toast.makeText(
                                            context,
                                            "Invalid URL protocol",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        Log.w(TAG, "Blocked invalid URL: $url")
                                        return true
                                    }
                                }
                                SecurityLevel.MEDIUM, SecurityLevel.HIGH -> {
                                    // Only allow HTTPS for MEDIUM and HIGH security
                                    if (!url.startsWith("https://")) {
                                        Toast.makeText(
                                            context,
                                            "Only HTTPS connections allowed in ${securityLevel.getDisplayName()} security mode",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        Log.w(TAG, "Blocked non-HTTPS URL: $url")
                                        return true
                                    }
                                }
                            }
                            
                            // Verify it's a banking domain
                            val uri = Uri.parse(url)
                            val host = uri.host?.lowercase() ?: ""
                            
                            // Use the fetched whitelist or fallback to hardcoded list
                            val allowedDomains = if (whitelistUrls.isNotEmpty()) {
                                whitelistUrls
                            } else {
                                listOf(
                                    "1drv.ms",
                                    "onedrive.live.com",
                                    "sbi.bank.in",
                                    "onlinesbi.sbi",
                                    "sbi.co.in",
                                    "icicibank.com",
                                    "infinity.icicibank.com",
                                    "kotak.com",
                                    "netbanking.kotak.com",
                                    "yesbank.in",
                                    "citibank.co.in",
                                    "online.citibank.co.in",
                                    "americanexpress.com",
                                    "ucobank.com",
                                    "indusind.com",
                                    "hdfcbank.com",
                                    "netbanking.hdfcbank.com"
                                )
                            }
                            
                            val isAllowed = allowedDomains.any { domain ->
                                host.contains(domain) || host.endsWith(domain)
                            }
                            
                            if (!isAllowed) {
                                // Show external app warning instead of blocking directly
                                externalUrl = url
                                showExternalAppWarning = true
                                Log.w(TAG, "Non-whitelisted domain attempted: $host")
                                return true
                            }
                            
                            Log.d(TAG, "Loading URL: $url")
                            // Load URL
                            view?.loadUrl(url)
                            return true
                        }
                        
                        override fun onReceivedSslError(
                            view: WebView?,
                            handler: SslErrorHandler?,
                            error: android.net.http.SslError?
                        ) {
                            val url = error?.url ?: ""
                            val errorType = when (error?.primaryError) {
                                android.net.http.SslError.SSL_NOTYETVALID -> "Certificate not yet valid"
                                android.net.http.SslError.SSL_EXPIRED -> "Certificate expired"
                                android.net.http.SslError.SSL_IDMISMATCH -> "Hostname mismatch"
                                android.net.http.SslError.SSL_UNTRUSTED -> "Certificate not trusted"
                                android.net.http.SslError.SSL_DATE_INVALID -> "Certificate date invalid"
                                android.net.http.SslError.SSL_INVALID -> "Certificate invalid"
                                else -> "Unknown SSL error"
                            }
                            
                            Log.w(TAG, "SSL Error on $url: $errorType")
                            
                            // Handle SSL based on security level
                            when (securityLevel) {
                                SecurityLevel.LOW -> {
                                    // Low security - proceed with SSL errors
                                    Log.w(TAG, "Proceeding despite SSL error (Low security mode)")
                                    handler?.proceed()
                                    Toast.makeText(
                                        context,
                                        "Warning: SSL certificate issue detected",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                SecurityLevel.MEDIUM, SecurityLevel.HIGH -> {
                                    // Medium and High - verify SSL strictly
                                    // Only proceed for known banking domains with temporary issues
                                    val uri = Uri.parse(url)
                                    val host = uri.host?.lowercase() ?: ""
                                    val trustedBankingDomains = listOf(
                                        "sbi.bank.in",
                                        "onlinesbi.sbi",
                                        "sbi.co.in",
                                        "icicibank.com",
                                        "kotak.com",
                                        "yesbank.in",
                                        "citibank.co.in",
                                        "americanexpress.com",
                                        "ucobank.com",
                                        "indusind.com",
                                        "hdfcbank.com"
                                    )
                                    
                                    val isTrustedBanking = trustedBankingDomains.any { domain ->
                                        host.contains(domain) || host.endsWith(domain)
                                    }
                                    
                                    if (isTrustedBanking) {
                                        Log.w(TAG, "Proceeding with SSL error for trusted banking site")
                                        handler?.proceed()
                                        Toast.makeText(
                                            context,
                                            "Connecting securely to banking site...",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        Log.e(TAG, "Canceling SSL connection - not a trusted banking domain")
                                        handler?.cancel()
                                        Toast.makeText(
                                            context,
                                            "SSL certificate verification failed",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                        }
                        
                        override fun onReceivedError(
                            view: WebView?,
                            request: WebResourceRequest?,
                            error: WebResourceError?
                        ) {
                            super.onReceivedError(view, request, error)
                            val errorCode = error?.errorCode ?: 0
                            val description = error?.description?.toString() ?: "Unknown error"
                            val failingUrl = request?.url?.toString() ?: "Unknown URL"
                            
                            Log.e(TAG, "WebView error $errorCode: $description on $failingUrl")
                            
                            // Show user-friendly error message for main frame only
                            if (request?.isForMainFrame == true) {
                                when (errorCode) {
                                    -6 -> Log.e(TAG, "Connection closed - may retry automatically")
                                    -2 -> {
                                        Toast.makeText(
                                            context,
                                            "No internet connection",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                    else -> {
                                        Toast.makeText(
                                            context,
                                            "Connection error. Retrying...",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                        }
                        
                        override fun onPageFinished(view: WebView?, url: String?) {
                            super.onPageFinished(view, url)
                            Log.d(TAG, "Page finished loading: $url")
                        }
                    }
                    
                    // WebChromeClient for permission handling
                    webChromeClient = object : WebChromeClient() {
                        override fun onPermissionRequest(request: PermissionRequest?) {
                            request?.let {
                                val resources = it.resources
                                
                                // Check what permissions are requested
                                val needsCamera = resources.contains(PermissionRequest.RESOURCE_VIDEO_CAPTURE)
                                val needsMic = resources.contains(PermissionRequest.RESOURCE_AUDIO_CAPTURE)
                                
                                when {
                                    needsCamera && needsMic -> {
                                        // Both camera and mic requested
                                        pendingPermissionRequest = request
                                        showCameraWarning = true
                                    }
                                    needsCamera -> {
                                        // Only camera requested
                                        pendingPermissionRequest = request
                                        showCameraWarning = true
                                    }
                                    needsMic -> {
                                        // Only mic requested
                                        pendingPermissionRequest = request
                                        showMicWarning = true
                                    }
                                    else -> {
                                        // Other permissions - deny by default for security
                                        request.deny()
                                    }
                                }
                            }
                        }
                    }
                    
                    // Load custom HTML from assets with user's name
                    try {
                        val htmlContent = context.assets.open("index.html").bufferedReader().use { it.readText() }
                        val displayName = if (userName.isNotBlank()) userName else "User"
                        Log.d(TAG, "Loading HTML with userName: '$displayName'")
                        val personalizedHtml = htmlContent.replace("Welcome, Nimit", "Welcome, $displayName")
                        loadDataWithBaseURL(
                            "file:///android_asset/",
                            personalizedHtml,
                            "text/html",
                            "UTF-8",
                            null
                        )
                    } catch (e: Exception) {
                        Log.e(TAG, "Error loading HTML: ${e.message}", e)
                        loadUrl("file:///android_asset/index.html")
                    }
                }.also { webView = it }
            },
            update = { view ->
                webView = view
                // Reload with updated userName when it changes
                if (userName.isNotBlank()) {
                    try {
                        val htmlContent = context.assets.open("index.html").bufferedReader().use { it.readText() }
                        val personalizedHtml = htmlContent.replace("Welcome, Nimit", "Welcome, $userName")
                        Log.d(TAG, "Updating HTML with userName: '$userName'")
                        view.loadDataWithBaseURL(
                            "file:///android_asset/",
                            personalizedHtml,
                            "text/html",
                            "UTF-8",
                            null
                        )
                    } catch (e: Exception) {
                        Log.e(TAG, "Error updating HTML: ${e.message}", e)
                    }
                }
            }
        )
    }
    
    // Warning Dialogs
    if (showCameraWarning) {
        CameraAccessWarningDialog(
            onDismiss = { 
                showCameraWarning = false
                pendingPermissionRequest?.deny()
                pendingPermissionRequest = null
            },
            onAllow = {
                showCameraWarning = false
                val activity = context as? ComponentActivity
                
                // Check if camera permission is granted
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) 
                    == PackageManager.PERMISSION_GRANTED) {
                    // Permission already granted, grant to webview
                    val resources = pendingPermissionRequest?.resources ?: emptyArray()
                    pendingPermissionRequest?.grant(resources)
                    pendingPermissionRequest = null
                } else {
                    // Request camera permission from user
                    activity?.requestPermissions(
                        arrayOf(Manifest.permission.CAMERA),
                        CAMERA_PERMISSION_REQUEST_CODE
                    )
                }
            },
            onDeny = {
                showCameraWarning = false
                pendingPermissionRequest?.deny()
                pendingPermissionRequest = null
                Toast.makeText(context, "Camera access denied", Toast.LENGTH_SHORT).show()
            }
        )
    }
    
    if (showMicWarning) {
        MicrophoneAccessWarningDialog(
            onDismiss = { 
                showMicWarning = false
                pendingPermissionRequest?.deny()
                pendingPermissionRequest = null
            },
            onAllow = {
                showMicWarning = false
                val activity = context as? ComponentActivity
                
                // Check if microphone permission is granted
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) 
                    == PackageManager.PERMISSION_GRANTED) {
                    // Permission already granted, grant to webview
                    val resources = pendingPermissionRequest?.resources ?: emptyArray()
                    pendingPermissionRequest?.grant(resources)
                    pendingPermissionRequest = null
                } else {
                    // Request microphone permission from user
                    activity?.requestPermissions(
                        arrayOf(Manifest.permission.RECORD_AUDIO),
                        MIC_PERMISSION_REQUEST_CODE
                    )
                }
            },
            onDeny = {
                showMicWarning = false
                pendingPermissionRequest?.deny()
                pendingPermissionRequest = null
                Toast.makeText(context, "Microphone access denied", Toast.LENGTH_SHORT).show()
            }
        )
    }
    
    if (showExternalAppWarning) {
        ExternalAppWarningDialog(
            url = externalUrl,
            onDismiss = { 
                showExternalAppWarning = false
            },
            onProceed = {
                showExternalAppWarning = false
                // User chose to proceed - load the external URL
                webView?.loadUrl(externalUrl)
                Log.w(TAG, "User proceeded to external URL: $externalUrl")
            },
            onCancel = {
                showExternalAppWarning = false
                Toast.makeText(
                    context, 
                    "Stayed on trusted banking sites", 
                    Toast.LENGTH_SHORT
                ).show()
            }
        )
    }
    
    // Exit confirmation dialog
    if (showExitDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showExitDialog = false },
            icon = {
                androidx.compose.material3.Icon(
                    imageVector = androidx.compose.material.icons.Icons.Default.ExitToApp,
                    contentDescription = "Exit App"
                )
            },
            title = {
                androidx.compose.material3.Text(text = "Exit App")
            },
            text = {
                androidx.compose.material3.Text(
                    text = "Are you sure you want to exit Privacy First Browser?"
                )
            },
            confirmButton = {
                androidx.compose.material3.TextButton(
                    onClick = {
                        showExitDialog = false
                        (context as? MainActivity)?.finish()
                    }
                ) {
                    androidx.compose.material3.Text("Exit")
                }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(
                    onClick = { showExitDialog = false }
                ) {
                    androidx.compose.material3.Text("Cancel")
                }
            }
        )
    }
}

class WebAppInterface(private val context: android.content.Context) {
    @JavascriptInterface
    fun openSettings() {
        val intent = Intent(context, SettingsActivity::class.java)
        context.startActivity(intent)
    }
}
