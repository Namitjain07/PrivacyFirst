# Quick Setup Guide

## Prerequisites
1. Node.js server running at `http://192.168.2.244:5001`
2. Android device/emulator on the same network
3. MongoDB database configured

## Setup Steps

### 1. Server Setup
```bash
cd api
node server.js
```

Server should start on port 5001.

### 2. Update Server IP (if needed)
If your server IP is different, update:
- File: `app/src/main/java/com/secure/privacyfirst/network/RetrofitClient.kt`
- Line: `private const val BASE_URL = "http://192.168.2.244:5001/"`
- Also update: `app/src/main/res/xml/network_security_config.xml`

### 3. Build the App
```bash
./gradlew clean build
```

### 4. Install on Device
```bash
./gradlew installDebug
```

Or use Android Studio:
- Click "Run" button
- Select your device/emulator

### 5. Test the Integration

#### Launch App
1. Open the app
2. Complete onboarding and setup
3. Navigate to WebView screen

#### Check Logs (Logcat)
Filter by tags:
- `WebViewScreen`
- `WhitelistRepository`
- `TokenManager`

Expected logs:
```
D/WebViewScreen: Fetching whitelist from server...
D/WhitelistRepository: Login successful, token saved
D/WhitelistRepository: Whitelist fetched successfully: 17 URLs
```

#### Test URL Loading
1. Navigate to a whitelisted URL (e.g., https://sbi.co.in)
   - Should load successfully
2. Try a non-whitelisted URL
   - Should show warning dialog

### 6. Verify API Calls

#### Test Login Endpoint
```bash
curl -X POST http://192.168.2.244:5001/api/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"pass123"}'
```

Expected response:
```json
{
  "token": "eyJhbGc...",
  "expiresIn": "1h"
}
```

#### Test Whitelist Endpoint
```bash
curl -X GET http://192.168.2.244:5001/api/whitelist/ \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

Expected response:
```json
{
  "urls": ["sbi.co.in", "icicibank.com", ...],
  "count": 17
}
```

## Troubleshooting

### App shows "Failed to load whitelist from server"

**Check:**
1. Server is running: `curl http://192.168.2.244:5001/api/login`
2. Phone is on same network as server
3. Firewall allows port 5001
4. IP address is correct

**Fix:**
- Update IP in `RetrofitClient.kt`
- Check server logs for errors
- Ensure MongoDB is connected

### "Cleartext HTTP traffic not permitted"

**Fix:**
Update `network_security_config.xml` with your server IP:
```xml
<domain-config cleartextTrafficPermitted="true">
    <domain includeSubdomains="true">YOUR_IP_HERE</domain>
</domain-config>
```

### "Authentication failed"

**Check:**
- Admin credentials in `.env` file
- JWT_SECRET is set
- Server logs for authentication errors

**Fix:**
Update credentials in `WhitelistRepository.kt`:
```kotlin
private const val ADMIN_USERNAME = "your_username"
private const val ADMIN_PASSWORD = "your_password"
```

### App crashes on startup

**Check Logcat for:**
- Network errors
- JSON parsing errors
- Missing dependencies

**Fix:**
```bash
./gradlew clean
./gradlew build
```

## Configuration Files

### Server (.env)
```env
MONGO_URI=mongodb+srv://...
ADMIN_USERNAME=admin
ADMIN_PASSWORD=pass123
JWT_SECRET=your_random_secret
PORT=5001
```

### App (RetrofitClient.kt)
```kotlin
private const val BASE_URL = "http://192.168.2.244:5001/"
```

### Network Security (network_security_config.xml)
```xml
<domain-config cleartextTrafficPermitted="true">
    <domain includeSubdomains="true">192.168.2.244</domain>
</domain-config>
```

## Next Steps

1. **Daily Refresh**: Implement WorkManager to refresh whitelist periodically
2. **Offline Support**: Cache whitelist in Room database
3. **Admin UI**: Add management screen for CRUD operations
4. **Production**: Change to HTTPS for production server

## Support

For issues:
1. Check server logs: `node server.js`
2. Check app logs: Android Studio Logcat
3. Verify network connectivity
4. Review WHITELIST_API_INTEGRATION.md for details
