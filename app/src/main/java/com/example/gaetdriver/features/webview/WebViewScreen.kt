package com.example.gaetdriver.features.webview

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.gaetdriver.core.base.i18n.LocalStrings
import com.example.gaetdriver.core.firebase.rememberAuthManager
import com.example.gaetdriver.core.ui.components.AppWebView
import com.example.gaetdriver.core.ui.components.SectionHeader
import com.example.gaetdriver.core.ui.layout.ViewLayout

/**
 * Screen that displays the driver's web portfolio using a lightweight WebView.
 */
@Composable
fun WebViewScreen() {
    val strings = LocalStrings.current
    val authManager = rememberAuthManager()
    val userId = authManager.currentUserId

    ViewLayout(
        header = {
            SectionHeader(title = strings.preview)
        },
        body = {
            if (userId != null) {
                AppWebView(
                    url = "https://gaetdriver.web.app/portfolio/$userId",
                    modifier = Modifier.fillMaxSize()
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
