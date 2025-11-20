package com.secure.privacyfirst.model

import androidx.annotation.DrawableRes
import com.secure.privacyfirst.R

data class OnboardingPage(
    val title: String,
    val description: String,
    @param:DrawableRes val image: Int
)

val onboardingPages = listOf(
    OnboardingPage(
        title = "Secure Banking",
        description = "Every time you access your bank, we redirect you only to official and trusted portals, keeping phishing attempts away.",
        image = R.drawable.onboarding_1
    ),
    OnboardingPage(
        title = "Privacy First",
        description = "Once you're done, all browsing data and session history are automatically erased, leaving no trace behind.",
        image = R.drawable.onboarding_2
    ),
    OnboardingPage(
        title = "Easy Access",
        description = "No complicated steps or logins. Just choose your bank and get safe, instant access to its official site.",
        image = R.drawable.onboarding_3
    )
)
