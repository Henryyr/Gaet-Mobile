package com.example.gaetdriver.features.onboarding.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.gaetdriver.core.data.model.DriverProfile
import com.example.gaetdriver.core.data.repository.rememberPortfolioRepository
import com.example.gaetdriver.core.ui.components.AppButton
import com.example.gaetdriver.core.ui.components.AppTextField
import kotlinx.coroutines.launch

/**
 * Shared content for setting up or editing the web portfolio.
 * Used in both Onboarding and Profile settings.
 */
@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun PortfolioSetupContent(
    userId: String,
    onComplete: () -> Unit
) {
    val portfolioRepo = rememberPortfolioRepository()
    val scope = rememberCoroutineScope()
    
    var profile by remember { mutableStateOf<DriverProfile?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var isSaving by remember { mutableStateOf(false) }

    // Form states
    var tagline by remember { mutableStateOf("") }
    var vehicle by remember { mutableStateOf("") }
    var experience by remember { mutableStateOf("") }
    val selectedServices = remember { mutableStateListOf<String>() }

    val availableServices = listOf("City Tour", "Airport Transfer", "Nature Trip", "Culture Tour", "Flexible Route", "Inter-city")

    LaunchedEffect(userId) {
        profile = portfolioRepo.getProfile(userId)
        profile?.let {
            tagline = it.tagline
            vehicle = it.vehicle
            experience = it.experience
            selectedServices.clear()
            selectedServices.addAll(it.services)
        }
        isLoading = false
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Web Portfolio Setup",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = "These details will define how your public portfolio page looks to potential clients.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            AppTextField(
                value = tagline,
                onValueChange = { tagline = it },
                label = "Your Professional Tagline",
                placeholder = "e.g. Your Personal Guide for Hidden Gems"
            )

            AppTextField(
                value = vehicle,
                onValueChange = { vehicle = it },
                label = "Vehicle Description",
                placeholder = "e.g. Modern SUV / AC Minibus"
            )

            AppTextField(
                value = experience,
                onValueChange = { experience = it },
                label = "Guiding Experience",
                placeholder = "e.g. 10 years exploring Java"
            )

            Text("Services", style = MaterialTheme.typography.titleSmall)
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                availableServices.forEach { service ->
                    FilterChip(
                        selected = selectedServices.contains(service),
                        onClick = { 
                            if (selectedServices.contains(service)) 
                                selectedServices.remove(service) 
                            else 
                                selectedServices.add(service) 
                        },
                        label = { Text(service) }
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            AppButton(
                text = "Save Portfolio Settings",
                icon = Icons.Default.Save,
                isLoading = isSaving,
                onClick = {
                    isSaving = true
                    scope.launch {
                        val updated = (profile ?: DriverProfile()).copy(
                            tagline = tagline,
                            vehicle = vehicle,
                            experience = experience,
                            services = selectedServices.toList(),
                            onboardingCompleted = true
                        )
                        portfolioRepo.saveProfile(userId, updated)
                        isSaving = false
                        onComplete()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(Modifier.height(32.dp))
        }
    }
}
