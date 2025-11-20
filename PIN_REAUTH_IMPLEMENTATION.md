# PIN Re-Authentication Feature

## Overview
This implementation ensures that users must re-enter their PIN every time they return to the app after switching to another app or going to the home screen.

## How It Works

### 1. **AuthStateManager** (`auth/AuthStateManager.kt`)
A singleton that manages the authentication state across the app:
- Tracks whether the user is currently authenticated
- Tracks whether re-authentication is required
- Provides methods to update authentication state when app goes to background/foreground

### 2. **MainActivity Lifecycle Observer**
The MainActivity now observes the app's lifecycle using `ProcessLifecycleOwner`:
- **onStop()**: Called when the app goes to background → marks that re-authentication is required
- **onStart()**: Called when the app returns to foreground → the app checks if authentication is needed

### 3. **AppNavigation Updates**
The navigation component monitors the `requiresAuth` state:
- When `requiresAuth` becomes true and the user is not authenticated, it automatically navigates to the Auth screen
- This happens regardless of which screen the user was on

### 4. **AuthScreen Updates**
The AuthScreen now updates the AuthStateManager when authentication succeeds:
- After successful PIN entry, it marks the user as authenticated
- After successful biometric authentication, it marks the user as authenticated

## User Experience

1. User opens the app and enters their PIN → authenticated
2. User presses home button or switches to another app → app goes to background
3. App detects background state and marks that re-authentication is required
4. User returns to the app → Auth screen is automatically shown
5. User must enter PIN again to continue

## Testing

To test this feature:
1. Open the app and log in with your PIN
2. Press the home button or switch to another app
3. Return to the app
4. You should see the PIN entry screen again

## Technical Notes

- Uses `ProcessLifecycleOwner` to observe app-wide lifecycle events (not just single Activity)
- State is managed using Kotlin StateFlow for reactive updates
- Navigation is handled automatically without requiring back stack manipulation
- Works seamlessly with both PIN and biometric authentication
