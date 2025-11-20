package com.secure.privacyfirst.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object Setup : Screen("setup")
    object WebView : Screen("webview")
    object Auth : Screen("auth")
    object Home : Screen("home")
    object PasswordManager : Screen("password_manager")
    object SetupPin : Screen("setup_pin")
}
//comment
