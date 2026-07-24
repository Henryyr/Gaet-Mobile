package com.example.gaetdriver.features.webview.ui

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.gaetdriver.constant.Variant
import com.example.gaetdriver.core.data.repository.rememberPortfolioRepository
import com.example.gaetdriver.core.firebase.rememberAuthManager
import com.example.gaetdriver.core.ui.components.AppButton
import com.example.gaetdriver.core.ui.components.AppCard
import kotlinx.coroutines.launch

// Master Theme Professional IDs (UID-style)
private const val ID_CLASSIC = "7j8K9L0m1N2p3Q4r5S6t"
private const val ID_MINIMALIST = "8v9W0x1Y2z3A4b5C6d7E"
private const val ID_EXPLORER = "9G0h1I2j3K4l5M6n7O8p"
private const val ID_EXECUTIVE = "0R1s2T3u4V5w6X7y8Z9a"
private const val ID_STORYTELLER = "1B2c3D4e5F6g7H8i9J0k"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebDesignFullScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val authManager = rememberAuthManager()
    val userId = authManager.currentUserId ?: ""
    val portfolioRepo = rememberPortfolioRepository()
    val scope = rememberCoroutineScope()

    var selectedPreset by remember { mutableStateOf(ID_CLASSIC) }
    var isLoading by remember { mutableStateOf(true) }
    var isSaving by remember { mutableStateOf(false) }

    // Load current preset ID
    LaunchedEffect(userId) {
        val profile = portfolioRepo.getProfile(userId)
        selectedPreset = profile?.themeId ?: ID_CLASSIC
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Design Web Components") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            Surface(tonalElevation = 8.dp) {
                AppButton(
                    text = "Save & Apply Design",
                    icon = Icons.Default.Save,
                    isLoading = isSaving,
                    onClick = {
                        isSaving = true
                        scope.launch {
                            try {
                                val profile = portfolioRepo.getProfile(userId)
                                if (profile != null) {
                                    val updated = profile.copy(themeId = selectedPreset)
                                    portfolioRepo.saveProfile(userId, updated)
                                    Toast.makeText(context, "Design Applied!", Toast.LENGTH_SHORT).show()
                                    onBack()
                                }
                            } catch (e: Exception) {
                                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                            } finally {
                                isSaving = false
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }
        }
    ) { innerPadding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Select a compact design preset to transform your web page. Each design is now fully responsive for mobile and desktop.",
                    style = MaterialTheme.typography.bodyMedium
                )

                PresetCard(
                    name = "Default Classic",
                    description = "The robust standard layout with a profile sidebar. Great for readability.",
                    isSelected = selectedPreset == ID_CLASSIC,
                    onClick = { selectedPreset = ID_CLASSIC }
                )

                PresetCard(
                    name = "Modern Minimalist",
                    description = "Ultra-clean and fast. Focuses on your story with bold typography.",
                    isSelected = selectedPreset == ID_MINIMALIST,
                    onClick = { selectedPreset = ID_MINIMALIST }
                )

                PresetCard(
                    name = "The Explorer",
                    description = "Rugged and nature-inspired. Perfect for adventure and scenic tours.",
                    isSelected = selectedPreset == ID_EXPLORER,
                    onClick = { selectedPreset = ID_EXPLORER }
                )

                PresetCard(
                    name = "The Executive",
                    description = "Premium high-contrast theme for VIP and corporate services.",
                    isSelected = selectedPreset == ID_EXECUTIVE,
                    onClick = { selectedPreset = ID_EXECUTIVE }
                )

                PresetCard(
                    name = "The Storyteller",
                    description = "Magazine-style journal layout that builds a personal connection.",
                    isSelected = selectedPreset == ID_STORYTELLER,
                    onClick = { selectedPreset = ID_STORYTELLER }
                )
            }
        }
    }
}

@Composable
private fun PresetCard(
    name: String,
    description: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    AppCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        variant = Variant.Outlined,
        containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface,
        border = if (isSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(text = description, style = MaterialTheme.typography.bodySmall)
            }
            if (isSelected) {
                Icon(Icons.Default.CheckCircle, null, tint = MaterialTheme.colorScheme.primary)
            }
        }
    }
}
