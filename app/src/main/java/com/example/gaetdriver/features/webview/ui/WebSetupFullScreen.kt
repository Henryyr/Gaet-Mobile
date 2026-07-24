package com.example.gaetdriver.features.webview.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.gaetdriver.core.base.i18n.LocalStrings
import com.example.gaetdriver.core.firebase.rememberAuthManager
import com.example.gaetdriver.features.onboarding.ui.PortfolioSetupContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebSetupFullScreen(
    onBack: () -> Unit
) {
    val strings = LocalStrings.current
    val authManager = rememberAuthManager()
    val userId = authManager.currentUserId ?: ""

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(strings.webSetup) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxHeight()) {
            PortfolioSetupContent(
                userId = userId,
                onComplete = onBack
            )
        }
    }
}
