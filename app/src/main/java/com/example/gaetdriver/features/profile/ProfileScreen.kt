package com.example.gaetdriver.features.profile
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Preview
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.gaetdriver.core.base.i18n.LocalStrings
import com.example.gaetdriver.core.firebase.AuthManager
import com.example.gaetdriver.core.ui.components.AppWebView
import com.example.gaetdriver.core.ui.components.SectionHeader
import com.example.gaetdriver.core.ui.layout.ViewLayout
import com.example.gaetdriver.features.profile.ui.BodyContent

@Composable
fun ProfileScreen(authManager: AuthManager) {
    val strings = LocalStrings.current
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Settings", "Web Preview")
    val userId = authManager.currentUserId

    ViewLayout(
        header = {
            Column {
                SectionHeader(title = strings.profileSettings)
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary,
                    divider = {}
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = { Text(title) },
                            icon = {
                                Icon(
                                    imageVector = if (index == 0) Icons.Default.Settings else Icons.Default.Preview,
                                    contentDescription = null
                                )
                            }
                        )
                    }
                }
            }
        },
        body = {
            Column(modifier = Modifier.fillMaxSize()) {
                when (selectedTab) {
                    0 -> BodyContent(authManager = authManager)
                    1 -> {
                        if (userId != null) {
                            AppWebView(
                                url = "https://gaetdriver.web.app/portfolio/$userId",
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Text("Please login to see preview", modifier = Modifier.padding(16.dp))
                        }
                    }
                }
            }
        }
    )
}

