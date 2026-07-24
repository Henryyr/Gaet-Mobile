package com.example.gaetdriver.features.webview.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.gaetdriver.features.onboarding.ui.PortfolioSetupContent

@Composable
fun WebEditContent(
    userId: String,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    PortfolioSetupContent(
        userId = userId,
        onComplete = onComplete
    )
}
