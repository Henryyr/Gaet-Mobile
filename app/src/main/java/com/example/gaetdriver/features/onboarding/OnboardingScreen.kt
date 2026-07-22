package com.example.gaetdriver.features.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.gaetdriver.features.onboarding.ui.OnboardingHeader
import com.example.gaetdriver.features.onboarding.ui.PortfolioSetupContent

/**
 * Onboarding screen that forces new users to complete their web portfolio setup.
 */
@Composable
fun OnboardingScreen(
    userId: String,
    onComplete: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            OnboardingHeader()
            
            PortfolioSetupContent(
                userId = userId,
                onComplete = onComplete
            )
        }
    }
}
