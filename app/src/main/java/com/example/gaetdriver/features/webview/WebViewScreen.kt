package com.example.gaetdriver.features.webview

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.gaetdriver.core.base.i18n.LocalStrings
import com.example.gaetdriver.core.ui.components.AppMenuCard
import com.example.gaetdriver.core.ui.components.SectionHeader
import com.example.gaetdriver.core.ui.layout.ViewLayout

@Composable
fun WebViewScreen(
    onNavigateFullScreen: (String) -> Unit
) {
    val strings = LocalStrings.current

    ViewLayout(
        header = {
            SectionHeader(title = "Web Portfolio")
        },
        body = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Manage how your portfolio looks online. Choose an action below.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                AppMenuCard(
                    title = strings.preview,
                    subtitle = "View your live web page as seen by clients.",
                    icon = Icons.Default.Visibility,
                    onClick = { onNavigateFullScreen("web_preview") }
                )

                AppMenuCard(
                    title = strings.webDesign,
                    subtitle = "Customize the HTML structure and presets.",
                    icon = Icons.Default.ColorLens,
                    onClick = { onNavigateFullScreen("web_design") }
                )
            }
        }
    )
}
