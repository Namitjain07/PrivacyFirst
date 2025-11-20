# Deep Linking & External URL Support

## Overview
The PrivacyFirst app now supports opening external URLs from other apps, browsers, and links. When you click a link anywhere in the system, you'll see an option to "Open with PrivacyFirst".

## Features Added

### 1. Intent Filters
- **HTTP/HTTPS URLs**: The app can now handle all http:// and https:// links
- **Custom Deep Links**: Support for custom `privacyfirst://open` URLs
- **Launch Mode**: Set to `singleTask` to prevent multiple instances

### 2. URL Handling
When a user clicks a link from:
- A web browser
- Email client
- Messaging app
- Any other app

The system will show "Open with PrivacyFirst" as an option.

## How It Works

### Android Manifest Changes
Added intent filters to `MainActivity`:
```xml
<!-- Deep link support for http/https URLs -->
<intent-filter android:autoVerify="true">
    <action android:name="android.intent.action.VIEW" />
    <category android:name="android.intent.category.DEFAULT" />
    <category android:name="android.intent.category.BROWSABLE" />
    <data android:scheme="http" />
    <data android:scheme="https" />
</intent-filter>

<!-- Custom app deep links -->
<intent-filter>
    <action android:name="android.intent.action.VIEW" />
    <category android:name="android.intent.category.DEFAULT" />
    <category android:name="android.intent.category.BROWSABLE" />
    <data android:scheme="privacyfirst" android:host="open" />
</intent-filter>
```

### MainActivity Updates
- Extracts URLs from incoming `ACTION_VIEW` intents
- Passes URL to the navigation system via `pendingUrlState`
- Handles both fresh app launches and already-running app scenarios via `onNewIntent()`

### Navigation Updates
- `AppNavigation` accepts optional `pendingUrlState` parameter
- Passes the pending URL to `WebViewScreen`

### WebViewScreen Updates
- Accepts optional `externalUrl` parameter
- Loads external URL if provided, otherwise loads default HTML page
- Maintains all existing security features (whitelist checking, SSL verification, etc.)

## Usage Examples

### From Other Apps
1. Click any web link in an email, message, or document
2. Select "Open with PrivacyFirst" from the dialog
3. The URL opens in PrivacyFirst's secure WebView

### Custom Deep Links
Create a deep link in your content:
```html
<a href="privacyfirst://open?url=https://example.com">Open in PrivacyFirst</a>
```

### Testing
You can test deep linking using ADB:
```bash
# Test HTTP link
adb shell am start -a android.intent.action.VIEW -d "https://www.example.com" com.secure.privacyfirst

# Test custom deep link
adb shell am start -a android.intent.action.VIEW -d "privacyfirst://open" com.secure.privacyfirst
```

## Security Considerations

### Whitelist Protection
- External URLs still respect the whitelist settings
- Non-whitelisted domains show a warning dialog
- Users must explicitly approve non-whitelisted URLs

### Security Levels
- **HIGH**: Screenshots blocked, downloads disabled
- **MEDIUM**: HTTPS enforced, SSL strictly verified
- **LOW**: HTTP allowed, more permissive SSL handling

### SSL Verification
All external URLs go through the same SSL verification process as internally navigated URLs.

## User Experience

1. **First Launch**: User clicks link → System shows "Open with" dialog → User selects PrivacyFirst
2. **App Running**: URL loads immediately in existing WebView
3. **Non-whitelisted URLs**: Warning dialog appears, user can approve or cancel

## Notes
- The app uses `singleTask` launch mode to prevent duplicate instances
- Existing navigation and authentication flows are preserved
- External URLs clear when user navigates away or closes the app
