package com.example.gaetdriver.features.profile.ui

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.gaetdriver.core.base.i18n.LocalStrings
import com.example.gaetdriver.core.firebase.AuthManager
import com.example.gaetdriver.core.data.repository.rememberPortfolioRepository
import com.example.gaetdriver.core.data.model.DriverProfile
import com.example.gaetdriver.core.ui.components.*
import com.example.gaetdriver.core.utils.DeviceManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BodyContent(authManager: AuthManager) {
    val strings = LocalStrings.current
    val context = LocalContext.current
    val deviceManager = remember { DeviceManager(context) }
    val portfolioRepo = rememberPortfolioRepository()
    val themeMode by deviceManager.themeMode.collectAsState(initial = "system")
    val scope = rememberCoroutineScope()

    var profile by remember { mutableStateOf<DriverProfile?>(null) }
    var isEditing by remember { mutableStateOf(false) }
    var showPreview by remember { mutableStateOf(false) }

    val userId = authManager.currentUserId

    LaunchedEffect(userId) {
        userId?.let { uid ->
            try {
                profile = portfolioRepo.getProfile(uid)
            } catch (_: Exception) {
                // Ignore profile fetch errors here, profile will remain null
            }
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Driver Portfolio",
                    style = MaterialTheme.typography.titleMedium
                )
                
                if (!isEditing && profile != null) {
                    TextButton(
                        onClick = { showPreview = true },
                        contentPadding = PaddingValues(horizontal = 12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Visibility,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Preview Web", style = MaterialTheme.typography.labelLarge)
                    }
                }
            }

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
                                    try {
                                        userId?.let { uid -> portfolioRepo.saveProfile(uid, it) }
                                        profile = it
                                        isEditing = false
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Update failed: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                                    }
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

    // In-App Web Preview Dialog
    if (showPreview && userId != null) {
        Dialog(
            onDismissRequest = { showPreview = false },
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                dismissOnBackPress = true,
                dismissOnClickOutside = false
            )
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Header with Close Button
                    CenterAlignedTopAppBar(
                        title = { Text("Web Portfolio Preview", style = MaterialTheme.typography.titleMedium) },
                        navigationIcon = {
                            IconButton(onClick = { showPreview = false }) {
                                Icon(Icons.Default.Close, contentDescription = "Close")
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    )
                    
                    // The Lightweight WebView
                    AppWebView(
                        url = "https://gaetdriver.web.app/portfolio/$userId",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}
