package com.example.gaetdriver.features.webview

import android.webkit.WebView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.gaetdriver.core.base.i18n.LocalStrings
import com.example.gaetdriver.core.firebase.rememberAuthManager
import com.example.gaetdriver.core.ui.components.AppWebView
import com.example.gaetdriver.core.ui.components.SectionHeader
import com.example.gaetdriver.core.ui.layout.ViewLayout

@Composable
fun WebViewScreen() {
    val strings = LocalStrings.current
    val authManager = rememberAuthManager()
    val userId = authManager.currentUserId

    var webView: WebView? by remember {
        mutableStateOf(null)
    }

    ViewLayout(
        header = {
            SectionHeader(
                title = strings.preview,
                trailingContent = {
                    IconButton(onClick = { webView?.reload() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reload",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        },
        body = {
            if (userId != null) {
                AppWebView(
                    url = "https://gaetdriver.web.app/portfolio/$userId",
                    modifier = Modifier.fillMaxSize(),
                    onWebViewCreated = {
                        webView = it
                    }
                )
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Please login to see preview",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    )
}