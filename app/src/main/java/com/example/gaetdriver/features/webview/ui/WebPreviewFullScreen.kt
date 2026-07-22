package com.example.gaetdriver.features.webview.ui

import android.webkit.WebView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.gaetdriver.core.base.i18n.LocalStrings
import com.example.gaetdriver.core.firebase.rememberAuthManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebPreviewFullScreen(
    onBack: () -> Unit
) {
    val strings = LocalStrings.current
    val authManager = rememberAuthManager()
    val userId = authManager.currentUserId
    
    var webView: WebView? by remember { mutableStateOf(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(strings.preview) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { webView?.reload() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Reload")
                    }
                }
            )
        }
    ) { innerPadding ->
        WebPreviewContent(
            userId = userId,
            onWebViewCreated = { webView = it },
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        )
    }
}
