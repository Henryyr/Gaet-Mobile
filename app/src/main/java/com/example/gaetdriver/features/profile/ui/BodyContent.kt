package com.example.gaetdriver.features.profile.ui

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.gaetdriver.core.base.AppException
import com.example.gaetdriver.core.base.i18n.LocalStrings
import com.example.gaetdriver.core.firebase.AuthManager
import com.example.gaetdriver.core.data.repository.rememberPortfolioRepository
import com.example.gaetdriver.core.data.model.DriverProfile
import com.example.gaetdriver.core.ui.components.*
import kotlinx.coroutines.launch

/**
 * Driver Bio & Info editing content.
 */
@Composable
fun BodyContent(authManager: AuthManager) {
    val strings = LocalStrings.current
    val context = LocalContext.current
    val portfolioRepo = rememberPortfolioRepository()
    val scope = rememberCoroutineScope()

    var profile by remember { mutableStateOf<DriverProfile?>(null) }
    var isEditing by remember { mutableStateOf(false) }

    val userId = authManager.currentUserId

    LaunchedEffect(userId) {
        userId?.let { uid ->
            try {
                profile = portfolioRepo.getProfile(uid)
            } catch (e: Exception) {
                val appException = AppException.from(e)
                Toast.makeText(context, appException.errorMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Portfolio Information",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary
            )

            if (!isEditing) {
                profile?.let { p ->
                    InfoItem(label = strings.firstName, value = p.firstName)
                    InfoItem(label = strings.lastName, value = p.lastName)
                    InfoItem(label = strings.email, value = p.email)
                    InfoItem(label = strings.phoneNumber, value = p.phone)
                    InfoItem(label = "Location", value = p.location.ifBlank { "Not set" })
                    InfoItem(label = "Bio", value = p.bio.ifBlank { "No bio added yet" })

                    AppButton(
                        text = "Edit Information",
                        onClick = { isEditing = true },
                        style = ButtonStyle.Outline,
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                    )
                }
            } else {
                var tempFirstName by remember { mutableStateOf(profile?.firstName ?: "") }
                var tempLastName by remember { mutableStateOf(profile?.lastName ?: "") }
                var tempPhone by remember { mutableStateOf(profile?.phone ?: "") }
                var tempBio by remember { mutableStateOf(profile?.bio ?: "") }
                var tempLocation by remember { mutableStateOf(profile?.location ?: "") }

                AppTextField(value = tempFirstName, onValueChange = { tempFirstName = it }, label = strings.firstName)
                AppTextField(value = tempLastName, onValueChange = { tempLastName = it }, label = strings.lastName)
                AppTextField(value = tempPhone, onValueChange = { tempPhone = it }, label = strings.phoneNumber)
                AppTextField(value = tempLocation, onValueChange = { tempLocation = it }, label = "Location")
                AppTextField(value = tempBio, onValueChange = { tempBio = it }, label = "Bio / About Me")

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 8.dp)) {
                    AppButton(
                        text = "Save",
                        onClick = {
                            val updated = profile?.copy(
                                firstName = tempFirstName,
                                lastName = tempLastName,
                                phone = tempPhone,
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
                                        val appException = AppException.from(e)
                                        Toast.makeText(context, "Update failed: ${appException.errorMessage}", Toast.LENGTH_LONG).show()
                                    }
                                }
                            }
                        },
                        modifier = Modifier.weight(1f)
                    )
                    AppButton(
                        text = strings.cancel,
                        onClick = { isEditing = false },
                        style = ButtonStyle.Outline,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoItem(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
        Text(text = value, style = MaterialTheme.typography.bodyLarge)
    }
}
