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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.gaetdriver.core.base.i18n.LocalStrings
import com.example.gaetdriver.core.firebase.AuthManager
import com.example.gaetdriver.core.data.repository.rememberPortfolioRepository
import com.example.gaetdriver.core.data.model.DriverProfile
import com.example.gaetdriver.core.ui.components.AppButton
import com.example.gaetdriver.core.ui.components.AppTextField
import com.example.gaetdriver.core.ui.components.ButtonStyle
import com.example.gaetdriver.core.utils.DeviceManager
import kotlinx.coroutines.launch

@Composable
fun BodyContent(authManager: AuthManager) {
    val strings = LocalStrings.current
    val context = LocalContext.current
    val deviceManager = remember { DeviceManager(context) }
    val portfolioRepo = rememberPortfolioRepository()
    val themeMode by deviceManager.themeMode.collectAsState(initial = "system")
    val scope = rememberCoroutineScope()

    var profile by remember { mutableStateOf<DriverProfile?>(null) }
    var isEditing by remember { mutableStateOf(value = false) }

    val userId = authManager.currentUserId

    LaunchedEffect(userId) {
        userId?.let { uid ->
            profile = portfolioRepo.getProfile(uid)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        // Profile Information Section
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Driver Portfolio",
                style = MaterialTheme.typography.titleMedium
            )

            if (!isEditing) {
                profile?.let { p ->
                    Text(text = "Full Name: ${p.fullName}", style = MaterialTheme.typography.titleLarge)
                    Text(text = "Email: ${p.email}")
                    Text(text = "Phone: ${p.phone}")
                    Text(text = "Location: ${p.location.ifBlank { "Location not set" }}")
                    Text(text = "Bio: ${p.bio.ifBlank { "No bio added yet" }}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    
                    AppButton(
                        text = "Update Portfolio",
                        onClick = { isEditing = true },
                        style = ButtonStyle.Outline,
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                    )
                }
            } else {
                var tempFirstName by remember { mutableStateOf(profile?.firstName ?: "") }
                var tempLastName by remember { mutableStateOf(profile?.lastName ?: "") }
                var tempBio by remember { mutableStateOf(profile?.bio ?: "") }
                var tempLocation by remember { mutableStateOf(profile?.location ?: "") }

                AppTextField(value = tempFirstName, onValueChange = { tempFirstName = it }, label = "First Name")
                AppTextField(value = tempLastName, onValueChange = { tempLastName = it }, label = "Last Name")
                AppTextField(value = tempLocation, onValueChange = { tempLocation = it }, label = "Location")
                AppTextField(value = tempBio, onValueChange = { tempBio = it }, label = "About Me / Bio")

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 8.dp)) {
                    AppButton(
                        text = "Save",
                        onClick = {
                            val updated = profile?.copy(
                                firstName = tempFirstName, 
                                lastName = tempLastName, 
                                bio = tempBio, 
                                location = tempLocation
                            )
                            updated?.let {
                                scope.launch {
                                    userId?.let { uid -> portfolioRepo.saveProfile(uid, it) }
                                    profile = it
                                    isEditing = false
                                }
                            }
                        },
                        modifier = Modifier.weight(1f)
                    )
                    AppButton(
                        text = "Cancel",
                        onClick = { isEditing = false },
                        style = ButtonStyle.Outline,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Theme Preference Section
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
        modifier = Modifier.fillMaxWidth(),
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
