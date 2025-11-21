# PrivacyFirst Browser

<div align="center">

![Version](https://img.shields.io/badge/version-1.0-blue.svg)
![API](https://img.shields.io/badge/API-24%2B-brightgreen.svg)
![License](https://img.shields.io/badge/license-Private-red.svg)

**A secure, privacy-focused mobile browser for Android with advanced banking security features**

</div>

## 📱 Overview

PrivacyFirst is a security-hardened Android browser application designed specifically for safe online banking and sensitive web browsing. Built with Jetpack Compose and Kotlin, it provides multi-layer security, whitelist protection, and zero-trace browsing to protect users from phishing attacks and unauthorized access.

## ✨ Key Features

### 🔐 Security Features
- **Multi-Level Security System** - Three configurable security levels (Low, Medium, High)
- **SSL/TLS Certificate Validation** - Strict HTTPS enforcement with certificate pinning
- **Screenshot Protection** - Prevents screen capture in high security mode
- **Biometric Authentication** - Fingerprint/Face unlock support
- **PIN-Based Security** - 4-digit PIN with automatic re-authentication
- **Session Auto-Lock** - Requires re-authentication when returning to app
- **Anti-Phishing Protection** - URL whitelist validation

### 🌐 Browsing Features
- **WebView Integration** - Full-featured web browsing with HTML5 support
- **Download Manager** - Secure file downloads with management interface
- **Deep Linking Support** - Open external URLs from other apps
- **Cookie Management** - Secure cookie storage and handling
- **Camera/Microphone Access** - Controlled permission dialogs
- **Custom User Agent** - Enhanced compatibility with banking sites

### 🔑 Password Management
- **Encrypted Password Storage** - AES-256 encryption for saved credentials
- **Password Manager UI** - Easy access to saved passwords
- **Search & Filter** - Quick password lookup
- **Auto-Fill Support** - Convenient credential access

### 🎨 User Experience
- **Material Design 3** - Modern, intuitive interface
- **Dark Mode Support** - Automatic theme adaptation
- **Onboarding Flow** - Smooth first-time user experience
- **Settings Management** - Customizable security and preferences
- **Edge-to-Edge Display** - Immersive full-screen experience

## 🏗️ Architecture

### Technology Stack
- **Language**: Kotlin 2.2.21
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM (Model-View-ViewModel)
- **Database**: Room 2.8.4
- **Networking**: Retrofit 3.0.0 + OkHttp
- **Security**: AndroidX Biometric 1.1.0
- **Navigation**: Navigation Compose 2.9.6
- **Data Storage**: DataStore Preferences 1.2.0

### Project Structure
```
PrivacyFirst/
├── app/                        # Android application
│   ├── src/main/
│   │   ├── java/.../privacyfirst/
│   │   │   ├── auth/          # Authentication logic
│   │   │   ├── data/          # Data models & database
│   │   │   ├── model/         # Domain models
│   │   │   ├── navigation/    # Navigation components
│   │   │   ├── network/       # API & networking
│   │   │   ├── ui/            # Compose UI components
│   │   │   │   ├── components/
│   │   │   │   ├── screens/
│   │   │   │   └── theme/
│   │   │   ├── utils/         # Utility classes
│   │   │   ├── viewmodel/     # ViewModels
│   │   │   ├── MainActivity.kt
│   │   │   ├── SettingsActivity.kt
│   │   │   └── PrivacyFirstApp.kt
│   │   ├── assets/            # HTML assets
│   │   └── res/               # Android resources
│   └── build.gradle
├── api/                        # Backend API server
│   ├── middleware/
│   ├── models/
│   ├── routes/
│   └── server.js
└── gradle/
```

## 🔧 Components

### Core Modules

#### Authentication (`auth/`)
- **AuthStateManager** - Manages app-wide authentication state
- **Biometric authentication** - Fingerprint/Face unlock integration
- **PIN authentication** - Encrypted PIN storage and validation

#### Data Layer (`data/`)
- **AppDatabase** - Room database configuration
- **PasswordDao** - Password CRUD operations
- **PinDao** - PIN management
- **PasswordEntity** - Password data model
- **PinEntity** - PIN data model
- **CryptoUtils** - AES encryption utilities
- **SecurityLevel** - Security level enum (LOW, MEDIUM, HIGH)
- **UserPreferencesManager** - DataStore preferences handling

#### Network Layer (`network/`)
- **RetrofitClient** - Retrofit configuration with caching
- **ApiService** - API endpoint definitions
- **ApiModels** - Request/Response data models
- **TokenManager** - JWT token management
- **WhitelistRepository** - Whitelist API operations

#### UI Components (`ui/`)
##### Screens
- **SplashScreen** - App launch screen
- **OnboardingScreen** - First-time user introduction
- **SetupScreen** - Initial setup (name, PIN)
- **AuthScreen** - PIN/Biometric authentication
- **WebViewScreen** - Main browsing interface
- **PasswordManagerScreen** - Saved passwords management
- **SetupPinScreen** - Change PIN
- **DownloadsScreen** - Downloaded files management
- **SettingsActivity** - App settings and configuration

##### Components
- **CameraAccessWarningDialog** - Camera permission dialog
- **MicrophoneAccessWarningDialog** - Microphone permission dialog
- **ExternalAppWarningDialog** - External app launch warning

#### Utilities (`utils/`)
- **DownloadManagerHelper** - File download management

## 🚀 Getting Started

### Prerequisites
- **Android Studio**: Arctic Fox or newer
- **JDK**: 8 or higher
- **Android SDK**: API 24+ (Android 7.0+)
- **Node.js**: 14+ (for backend server)
- **MongoDB**: 4.4+ (for whitelist storage)

### Installation

#### 1. Clone the Repository
```bash
git clone https://github.com/Namitjain07/PrivacyFirst.git
cd PrivacyFirst
```

#### 2. Backend Setup
```bash
cd api
npm install

# Create .env file
cat > .env << EOF
MONGO_URI=mongodb+srv://your-mongodb-uri
ADMIN_USERNAME=admin
ADMIN_PASSWORD=Byf8$G&F*G8vGEfuhfuhfEHU!89f2qfiHT88%ffyutf7^s
JWT_SECRET=your_random_secret_key
PORT=5001
EOF

# Start the server
node server.js
```

#### 3. Configure Network Settings
Update the server IP in the app:

**File**: `app/src/main/java/com/secure/privacyfirst/network/RetrofitClient.kt`
```kotlin
private const val BASE_URL = "http://YOUR_SERVER_IP:5001/"
```

**File**: `app/src/main/res/xml/network_security_config.xml`
```xml
<domain-config cleartextTrafficPermitted="true">
    <domain includeSubdomains="true">YOUR_SERVER_IP</domain>
</domain-config>
```

#### 4. Build the App
```bash
./gradlew clean build
```

#### 5. Install on Device
```bash
./gradlew installDebug
```

Or use Android Studio:
- Open project in Android Studio
- Click "Run" button
- Select your device/emulator

## 🎯 Usage

### First Launch
1. **Onboarding** - Swipe through feature introduction
2. **Setup** - Enter your name and create a 4-digit PIN
3. **Main Screen** - Browse to banking websites from the home page

### Security Levels

#### 🟢 Low Security
- HTTPS connections only
- File downloads enabled
- Basic security level

#### 🟡 Medium Security (Default)
- HTTPS connections only
- SSL certificate verification
- File downloads enabled
- Enhanced security for banking

#### 🔴 High Security
- HTTPS connections only
- SSL certificate verification
- Screenshot protection enabled
- File downloads blocked
- Copy/paste disabled in WebView
- Maximum protection for sensitive operations

### Password Manager
1. Navigate to **Settings → Password Management → Password Manager**
2. Add credentials with **+ icon**
3. Search, view, edit, or delete saved passwords
4. All passwords encrypted with AES-256

### Downloads
1. Download files from websites (when allowed by security level)
2. View downloads in **Settings → Storage → Downloads**
3. Open, share, or delete downloaded files

### Deep Linking
- Click HTTP/HTTPS links in other apps
- Select "Open with PrivacyFirst"
- URL opens in secure WebView with whitelist validation

## 🔐 Security Implementation

### Encryption
- **Algorithm**: AES-256-GCM
- **Key Storage**: Android Keystore System
- **Password Hashing**: Encrypted storage with secure key generation

### Authentication Flow
1. User launches app
2. Check if setup completed
3. Prompt for PIN or biometric authentication
4. Validate credentials
5. Grant access to WebView
6. Auto-lock when app goes to background

### Whitelist Protection
1. Fetch whitelist from server on app start
2. Cache whitelist (5-minute TTL)
3. Validate URLs before loading
4. Show warning dialog for non-whitelisted sites
5. User can approve or reject navigation

### SSL Validation
```kotlin
// Medium/High security levels
override fun onReceivedSslError(
    view: WebView?,
    handler: SslErrorHandler?,
    error: SslError?
) {
    // Strict SSL validation - do not proceed
    handler?.cancel()
}
```

## 📡 API Integration

### Endpoints

#### Authentication
```
POST /api/login
Body: { "username": "admin", "password": "..." }
Response: { "token": "JWT_TOKEN", "expiresIn": "1h" }
```

#### Whitelist Management
```
GET /api/whitelist
Header: Authorization: Bearer <token>
Response: { "urls": [...], "count": 17 }

POST /api/whitelist/add
Body: { "url": "https://example.com" }

PUT /api/whitelist/update
Body: { "oldUrl": "...", "newUrl": "..." }

DELETE /api/whitelist/delete
Body: { "url": "..." }
```

### Caching Strategy
- **HTTP Cache**: 10 MB, 5-minute TTL
- **In-Memory Cache**: 5-minute TTL
- **Offline Support**: Serves stale cache up to 7 days
- **Auto-Retry**: 3 attempts with exponential backoff
- **Performance**: 90% reduction in API calls

## 🛠️ Configuration

### Security Level
Change in **Settings → Security → Security Level**

### Admin Credentials
**File**: `app/src/main/java/com/secure/privacyfirst/network/WhitelistRepository.kt`
```kotlin
private const val ADMIN_USERNAME = "admin"
private const val ADMIN_PASSWORD = "Byf8$G&F*G8vGEfuhfuhfEHU!89f2qfiHT88%ffyutf7^s"
```

### Cache Duration
**File**: `app/src/main/java/com/secure/privacyfirst/network/WhitelistRepository.kt`
```kotlin
private val cacheDuration = 5 * 60 * 1000L // 5 minutes
```

## 📊 Performance Metrics

| Metric | Value |
|--------|-------|
| Min SDK | 24 (Android 7.0) |
| Target SDK | 36 (Android 14) |
| App Size | ~15 MB |
| Startup Time | < 2 seconds |
| API Response (cached) | ~50 ms |
| API Response (network) | ~2-3 seconds |
| Network Call Reduction | 90% (with caching) |

## 🧪 Testing

### Test Deep Linking
```bash
# Test HTTP link
adb shell am start -a android.intent.action.VIEW -d "https://www.example.com" com.secure.privacyfirst

# Test custom deep link
adb shell am start -a android.intent.action.VIEW -d "privacyfirst://open" com.secure.privacyfirst
```

### Test API
```bash
# Login
curl -X POST http://192.168.2.244:5001/api/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"Byf8$G&F*G8vGEfuhfuhfEHU!89f2qfiHT88%ffyutf7^s"}'

# Get Whitelist
curl -X GET http://192.168.2.244:5001/api/whitelist \
  -H "Authorization: Bearer YOUR_TOKEN"
```

## 📝 Development

### Build Variants
- **Debug**: Development build with debugging enabled
- **Release**: Production build with ProGuard optimization

### Dependencies
See `gradle/libs.versions.toml` for complete dependency list.

**Key Dependencies:**
- Jetpack Compose BOM: 2025.11.01
- Kotlin: 2.2.21
- Room: 2.8.4
- Retrofit: 3.0.0
- Navigation Compose: 2.9.6
- Biometric: 1.1.0

### Code Style
- Kotlin coding conventions
- Material Design 3 guidelines
- MVVM architecture pattern
- Repository pattern for data access

## 📚 Documentation

Detailed documentation available in the repository:

- [Setup Guide](SETUP_GUIDE.md) - Quick setup instructions
- [API Documentation](api/API_DOCUMENTATION.md) - Complete API reference
- [Whitelist Integration](WHITELIST_API_INTEGRATION.md) - API integration details
- [Quick Reference](QUICK_REFERENCE.md) - Common usage patterns
- [Optimization Summary](OPTIMIZATION_SUMMARY.md) - Performance optimizations
- [Deep Linking Guide](DEEP_LINKING_GUIDE.md) - Deep linking implementation
- [Downloads Feature](DOWNLOADS_FEATURE.md) - Downloads feature overview
- [Downloads Developer Docs](DOWNLOADS_DEVELOPER_DOCS.md) - Downloads implementation
- [Downloads UI Guide](DOWNLOADS_UI_GUIDE.md) - Downloads UI details
- [PIN Re-authentication](PIN_REAUTH_IMPLEMENTATION.md) - Authentication flow

## 🔒 Privacy Policy

PrivacyFirst is designed with privacy as the core principle:

- **No Tracking**: Zero analytics or tracking
- **Local Storage**: All data stored locally on device
- **Encrypted Data**: Passwords encrypted with AES-256
- **Session Clearing**: Automatic data cleanup
- **No Third-Party**: No external service integration except whitelist API

## 🤝 Contributing

This is a private project. For feature requests or bug reports, please contact the repository owner.

## 📄 License

Private - All rights reserved.

## 👨‍💻 Author

**Namit Jain**
- GitHub: [@Namitjain07](https://github.com/Namitjain07)

## 🙏 Acknowledgments

- AndroidX team for Jetpack Compose
- Square for Retrofit and OkHttp
- Material Design team for design guidelines
- Room persistence library team

## 📞 Support

For issues or questions:
1. Check documentation in the repo
2. Review [SETUP_GUIDE.md](SETUP_GUIDE.md)
3. Check Android Studio Logcat for errors
4. Verify server logs for API issues

## 🗺️ Roadmap

### Planned Features
- [ ] Backup and restore functionality
- [ ] Tablet optimization
- [ ] Multi-language support
- [ ] Advanced whitelist management UI
- [ ] Browser history (encrypted)
- [ ] Bookmark management
- [ ] Tab management
- [ ] Reader mode
- [ ] Night mode for WebView
- [ ] VPN integration

### Future Enhancements
- [ ] Cloud sync for passwords (encrypted)
- [ ] Password strength analyzer
- [ ] Breach detection
- [ ] Two-factor authentication
- [ ] Export/import passwords
- [ ] Auto-fill framework integration

---

<div align="center">

**Built with ❤️ for Security and Privacy**

</div>
