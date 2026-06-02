package com.example.gaetdriver.features.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.gaetdriver.core.base.i18n.LocalStrings
import com.example.gaetdriver.core.firebase.AuthManager
import com.example.gaetdriver.core.ui.components.*
import com.example.gaetdriver.core.ui.layout.ViewLayout
import com.example.gaetdriver.features.profile.ui.BodyContent
import com.example.gaetdriver.core.utils.DeviceManager
import com.example.gaetdriver.features.profile.ui.ProfileMenuButton
import com.example.gaetdriver.features.profile.ui.ProfileToggleItem
import com.example.gaetdriver.features.profile.ui.ThemeOptionItem
import kotlinx.coroutines.launch

/**
 * Main Profile Screen with modular menu buttons.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(authManager: AuthManager) {
    val strings = LocalStrings.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val deviceManager = remember { DeviceManager(context) }
    
    // Observed Settings
    val themeMode by deviceManager.themeMode.collectAsState(initial = "system")
    val isSwipeNavEnabled by deviceManager.isSwipeNavEnabled.collectAsState(initial = true)
    val userLang by deviceManager.appLanguage.collectAsState(initial = null)
    
    // UI Local States
    var showEditProfile by remember { mutableStateOf(false) }
    var showThemeSheet by remember { mutableStateOf(false) }
    var showLanguageSheet by remember { mutableStateOf(false) }

    ViewLayout(
        header = {
            SectionHeader(title = strings.profileSettings)
        },
        body = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. Driver Portfolio settings
                ProfileMenuButton(
                    title = "Driver Bio & Info",
                    subtitle = "Manage your portfolio identity",
                    icon = Icons.Default.Badge,
                    onClick = { showEditProfile = true }
                )

                // 2. Language selection
                ProfileMenuButton(
                    title = "App Language",
                    subtitle = if (userLang == "id") "Bahasa Indonesia" else "English",
                    icon = Icons.Default.Language,
                    onClick = { showLanguageSheet = true }
                )

                // 3. Theme preference
                ProfileMenuButton(
                    title = strings.themePreference,
                    subtitle = "Current: ${themeMode.replaceFirstChar { it.uppercase() }}",
                    icon = Icons.Default.Palette,
                    onClick = { showThemeSheet = true }
                )

                // 4. Swipe navigation toggle
                ProfileToggleItem(
                    title = "Swipe Navigation",
                    subtitle = "Enable/Disable side swiping",
                    icon = Icons.Default.Swipe,
                    checked = isSwipeNavEnabled,
                    onToggle = { enabled ->
                        scope.launch { deviceManager.setSwipeNavEnabled(enabled) }
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Logout
                Button(
                    onClick = { authManager.signOut() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.error
                    ),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(strings.logout)
                }
            }
        }
    )

    // Modal Bottom Sheets
    if (showEditProfile) {
        ModalBottomSheet(
            onDismissRequest = { showEditProfile = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            Box(modifier = Modifier.fillMaxHeight(0.9f).padding(16.dp)) {
                BodyContent(authManager = authManager)
            }
        }
    }

    if (showThemeSheet) {
        ModalBottomSheet(onDismissRequest = { showThemeSheet = false }) {
            Column(modifier = Modifier.padding(16.dp).padding(bottom = 32.dp)) {
                Text("Select Theme", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(bottom = 16.dp))
                ThemeOptionItem("Light Mode", themeMode == "light") {
                    scope.launch { deviceManager.saveThemeMode("light"); showThemeSheet = false }
                }
                ThemeOptionItem("Dark Mode", themeMode == "dark") {
                    scope.launch { deviceManager.saveThemeMode("dark"); showThemeSheet = false }
                }
                ThemeOptionItem("System Default", themeMode == "system") {
                    scope.launch { deviceManager.saveThemeMode("system"); showThemeSheet = false }
                }
            }
        }
    }

    if (showLanguageSheet) {
        ModalBottomSheet(onDismissRequest = { showLanguageSheet = false }) {
            Column(modifier = Modifier.padding(16.dp).padding(bottom = 32.dp)) {
                Text("Select Language", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(bottom = 16.dp))
                ThemeOptionItem("English", userLang == "en") {
                    scope.launch { deviceManager.saveLanguage("en"); showLanguageSheet = false }
                }
                ThemeOptionItem("Bahasa Indonesia", userLang == "id") {
                    scope.launch { deviceManager.saveLanguage("id"); showLanguageSheet = false }
                }
                ThemeOptionItem("System Default", userLang == null || userLang == "") {
                    scope.launch { deviceManager.saveLanguage(""); showLanguageSheet = false }
                }
            }
        }
    }
}
