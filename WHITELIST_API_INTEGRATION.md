# PrivacyFirst - Whitelist API Integration

## Overview
This Android app now fetches whitelisted URLs from a remote server using Retrofit and Gson. The app securely authenticates with JWT tokens and dynamically loads the whitelist on startup.

## Server Configuration

### Base URL
The app connects to: `http://192.168.2.244:5001`

You can change this in: `app/src/main/java/com/secure/privacyfirst/network/RetrofitClient.kt`

### API Endpoints Used

1. **POST /api/login** - Get JWT token
2. **GET /api/whitelist/** - Fetch whitelisted URLs
3. **POST /api/whitelist/add** - Add new URL (admin)
4. **PUT /api/whitelist/update** - Update URL (admin)
5. **DELETE /api/whitelist/delete** - Delete URL (admin)

## Dependencies Added

### Gradle Version Catalog (`gradle/libs.versions.toml`)
```toml
[versions]
retrofit = "2.11.0"
okhttp = "4.12.0"
gson = "2.11.0"

[libraries]
retrofit = { module = "com.squareup.retrofit2:retrofit", version.ref = "retrofit" }
retrofit-converter-gson = { module = "com.squareup.retrofit2:converter-gson", version.ref = "retrofit" }
okhttp-logging = { module = "com.squareup.okhttp3:logging-interceptor", version.ref = "okhttp" }
gson = { module = "com.google.code.gson:gson", version.ref = "gson" }
```

### Build.gradle
```gradle
implementation libs.retrofit
implementation libs.retrofit.converter.gson
implementation libs.okhttp.logging
implementation libs.gson
```

## Architecture

### Network Layer

1. **ApiModels.kt** - Request and response data classes
   - `LoginRequest`, `LoginResponse`
   - `WhitelistResponse`
   - `AddUrlRequest`, `UpdateUrlRequest`, `DeleteUrlRequest`

2. **ApiService.kt** - Retrofit interface
   - Defines all API endpoints
   - Uses suspend functions for coroutines

3. **RetrofitClient.kt** - Singleton Retrofit instance
   - Configures OkHttp with logging
   - Sets base URL and timeouts
   - Adds Gson converter

4. **TokenManager.kt** - JWT token management
   - Securely stores token using DataStore
   - Manages token expiry
   - Provides auth headers

5. **WhitelistRepository.kt** - Business logic
   - Handles login and token refresh
   - Fetches whitelist from server
   - Manages CRUD operations for URLs

### WebView Integration

The `WebViewScreen.kt` now:
- Fetches whitelist on app start using `LaunchedEffect`
- Automatically logs in and retrieves JWT token
- Uses server whitelist for URL validation
- Falls back to hardcoded list if server is unavailable
- Shows toast notifications for load status

## Network Security

### Cleartext Traffic
The app allows HTTP traffic ONLY for the API server IP:

**network_security_config.xml:**
```xml
<domain-config cleartextTrafficPermitted="true">
    <domain includeSubdomains="true">192.168.2.244</domain>
    <domain includeSubdomains="true">localhost</domain>
    <domain includeSubdomains="true">10.0.2.2</domain>
</domain-config>
```

All banking sites remain HTTPS-only for security.

## Usage Flow

1. **App Startup**
   - WebView screen loads
   - Automatically calls `whitelistRepository.getWhitelist()`
   - If no token or expired, automatically calls `login()`
   - Stores JWT token securely in DataStore

2. **URL Loading**
   - User navigates to a URL
   - App checks if URL host matches any whitelisted domain
   - If yes: loads URL
   - If no: shows warning dialog

3. **Daily Refresh** (Recommended)
   - Implement periodic refresh using WorkManager
   - Fetch updated whitelist every 24 hours
   - Update local cache

## Security Features

✅ **JWT Authentication** - Bearer token for all API calls
✅ **Secure Storage** - Tokens stored in encrypted DataStore
✅ **Token Expiry** - Automatic re-authentication when expired
✅ **HTTPS Enforcement** - Banking sites always use HTTPS
✅ **Cleartext Restricted** - Only API server allows HTTP

## Credentials

### Default Admin Login
- **Username:** `admin`
- **Password:** `pass123`

Update these in `WhitelistRepository.kt` or use environment variables.

## Testing

### Test API Connection
1. Ensure server is running at `http://192.168.2.244:5001`
2. Test login: `POST http://192.168.2.244:5001/api/login`
3. Launch app and check Logcat for:
   - "Fetching whitelist from server..."
   - "Login successful, token saved"
   - "Whitelist loaded: X URLs"

### Check Logs
```
Tag: WebViewScreen
Tag: WhitelistRepository
Tag: TokenManager
```

## Troubleshooting

### "Failed to load whitelist from server"
- Check server is running
- Verify IP address is correct (192.168.2.244)
- Ensure phone is on same network
- Check firewall settings

### "No authentication token"
- Login may have failed
- Check admin credentials
- Verify JWT_SECRET is set on server

### Cleartext Traffic Error
- Verify network_security_config.xml includes server IP
- Check usesCleartextTraffic settings

## Future Enhancements

1. **WorkManager Integration**
   - Schedule daily whitelist refresh
   - Background sync when app is closed

2. **Caching Strategy**
   - Cache whitelist in Room database
   - Offline support with last known whitelist

3. **Admin Panel**
   - Add UI for managing whitelist
   - CRUD operations from within app

4. **Analytics**
   - Track blocked URLs
   - Monitor whitelist usage

## Files Modified/Created

### Created:
- `app/src/main/java/com/secure/privacyfirst/network/ApiModels.kt`
- `app/src/main/java/com/secure/privacyfirst/network/ApiService.kt`
- `app/src/main/java/com/secure/privacyfirst/network/RetrofitClient.kt`
- `app/src/main/java/com/secure/privacyfirst/network/TokenManager.kt`
- `app/src/main/java/com/secure/privacyfirst/network/WhitelistRepository.kt`

### Modified:
- `gradle/libs.versions.toml` - Added Retrofit, Gson, OkHttp versions
- `app/build.gradle` - Added dependencies
- `app/src/main/res/xml/network_security_config.xml` - Allow cleartext for API server
- `app/src/main/java/com/secure/privacyfirst/ui/screens/WebViewScreen.kt` - Integrated whitelist fetching

## Version Information

- **Retrofit**: 2.11.0 (Latest stable as of Nov 2024)
- **OkHttp**: 4.12.0 (Latest stable)
- **Gson**: 2.11.0 (Latest stable)
- **Kotlin**: 2.2.21
- **Compose**: 2025.11.00

All versions verified as latest stable releases.
