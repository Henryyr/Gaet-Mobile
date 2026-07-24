package com.example.gaetdriver.features.webview.ui

import android.webkit.WebView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.gaetdriver.core.ui.components.AppWebView

@Composable
fun WebPreviewContent(
    userId: String?,
    onWebViewCreated: (WebView) -> Unit,
    modifier: Modifier = Modifier
) {
    val timestamp = remember { System.currentTimeMillis() }
    
    if (userId != null) {
        AppWebView(
            url = "https://gaetdriver.web.app/portfolio/$userId?t=$timestamp",
            modifier = modifier.fillMaxSize(),
            onWebViewCreated = onWebViewCreated
        )
    } else {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Please login to see preview",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
