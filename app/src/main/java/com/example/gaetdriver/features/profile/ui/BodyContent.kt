package com.example.gaetdriver.features.profile.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.gaetdriver.core.base.i18n.LocalStrings
import com.example.gaetdriver.core.firebase.AuthManager
import com.example.gaetdriver.core.ui.components.AppButton
import com.example.gaetdriver.core.ui.components.ButtonStyle
import com.example.gaetdriver.core.utils.DeviceManager
import kotlinx.coroutines.launch

@Composable
fun BodyContent(authManager: AuthManager) {

    val strings = LocalStrings.current
    val context = LocalContext.current
    val deviceManager = remember { DeviceManager(context) }
    val themeMode by deviceManager.themeMode.collectAsState(initial = "system")
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = strings.themePreference,
                    style = MaterialTheme.typography.titleMedium
                )

                ThemeOption(strings.systemDefault, themeMode == "system") {
                    scope.launch { deviceManager.saveThemeMode("system") }
                }

                ThemeOption(strings.lightMode, themeMode == "light") {
                    scope.launch { deviceManager.saveThemeMode("light") }
                }

                ThemeOption(strings.darkMode, themeMode == "dark") {
                    scope.launch { deviceManager.saveThemeMode("dark") }
                }

                AppButton(
                    text = strings.logout,
                    onClick = { authManager.signOut() },
                    style = ButtonStyle.Outline,
                    modifier = Modifier.fillMaxWidth()
                )

            }
        }

    }

@Composable
fun ThemeOption(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label)
        RadioButton(
            selected = isSelected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(
                selectedColor = MaterialTheme.colorScheme.primary
            )
        )
    }
}