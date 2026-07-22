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
import androidx.compose.ui.unit.dp
import com.example.gaetdriver.core.constant.ExperienceConstants
import com.example.gaetdriver.core.data.model.DriverProfile
import com.example.gaetdriver.core.data.repository.rememberPortfolioRepository
import com.example.gaetdriver.core.ui.components.AppButton
import com.example.gaetdriver.core.ui.components.AppDropDown
import com.example.gaetdriver.core.ui.components.AppTextField
import com.example.gaetdriver.core.constant.VehicleConstants
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
    var selectedVehicleDropdown by remember { mutableStateOf("") }
    var selectedExperienceOptions by remember { mutableStateOf("")}
    var manualVehicleInput by remember { mutableStateOf("") }
    var experience by remember { mutableStateOf("") }
    val selectedServices = remember { mutableStateListOf<String>() }

    val availableServices = listOf("City Tour", "Airport Transfer", "Nature Trip", "Culture Tour", "Flexible Route", "Inter-city")

    LaunchedEffect(userId) {
        profile = portfolioRepo.getProfile(userId)
        profile?.let {
            tagline = it.tagline
            experience = it.experience
            selectedServices.clear()
            selectedServices.addAll(it.services)
            
            // Logic for vehicle dropdown vs manual input
            if (VehicleConstants.guideVehicleOptions.contains(it.vehicle)) {
                selectedVehicleDropdown = it.vehicle
                manualVehicleInput = ""
            } else if (it.vehicle.isNotEmpty()) {
                selectedVehicleDropdown = "Other"
                manualVehicleInput = it.vehicle
            }
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
            AppTextField(
                value = tagline,
                onValueChange = { tagline = it },
                label = "Your Professional Tagline",
                placeholder = "e.g. Your Personal Guide for Hidden Gems"
            )

            AppDropDown(
                options = VehicleConstants.guideVehicleOptions,
                selectedOption = selectedVehicleDropdown,
                onOptionSelected = { 
                    selectedVehicleDropdown = it
                    if (it != "Other") manualVehicleInput = ""
                },
                label = "Vehicle Type",
                placeholder = "Select your vehicle"
            )

            if (selectedVehicleDropdown == "Other") {
                AppTextField(
                    value = manualVehicleInput,
                    onValueChange = { manualVehicleInput = it },
                    label = "Specify Vehicle",
                    placeholder = "Enter your vehicle model"
                )
            }

            AppDropDown(
                options = ExperienceConstants.experienceOptions,
                selectedOption = selectedExperienceOptions,
                onOptionSelected = {
                    selectedExperienceOptions = it
                },
                label = "Guiding Experience",
                placeholder = "Years of Experience"
            )

            Text("Services", style = MaterialTheme.typography.titleSmall)
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                availableServices.forEach { service ->

                    val selected = selectedServices.contains(service)

                    FilterChip(
                        selected = selected,
                        enabled = selected || selectedServices.size < 3,
                        onClick = { 
                            if (selected)
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
                        val finalVehicle = if (selectedVehicleDropdown == "Other") {
                            manualVehicleInput
                        } else {
                            selectedVehicleDropdown
                        }
                        
                        val updated = (profile ?: DriverProfile()).copy(
                            tagline = tagline,
                            vehicle = finalVehicle,
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
