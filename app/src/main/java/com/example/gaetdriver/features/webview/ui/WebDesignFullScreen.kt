package com.example.gaetdriver.features.webview.ui

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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

    var selectedPreset by remember { mutableStateOf("default") }
    var isSaving by remember { mutableStateOf(false) }

    // Load current preset
    LaunchedEffect(userId) {
        val profile = portfolioRepo.getProfile(userId)
        selectedPreset = if (profile?.customHtml.isNullOrEmpty()) "default" else "modern"
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
                                    val updated = profile.copy(
                                        customHtml = if (selectedPreset == "modern") modernPresetHtml else ""
                                    )
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
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Select a design preset to transform your web page. New presets will override your current standard layout.",
                style = MaterialTheme.typography.bodyMedium
            )

            PresetCard(
                name = "Default Classic",
                description = "The standard layout with a clean hero and catalog grid.",
                isSelected = selectedPreset == "default",
                onClick = { selectedPreset = "default" }
            )

            PresetCard(
                name = "Modern Dark Minimalist",
                description = "A bold dark theme with card-based layouts and sleek animations.",
                isSelected = selectedPreset == "modern",
                onClick = { selectedPreset = "modern" }
            )
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

private val modernPresetHtml = """
<style>
    :root {
        --primary: #bb86fc;
        --bg: #121212;
        --surface: #1e1e1e;
        --text: #ffffff;
    }
    body { background-color: var(--bg); color: var(--text); font-family: 'Inter', sans-serif; margin: 0; }
    .hero { padding: 4rem 2rem; background: linear-gradient(135deg, #1e1e1e, #121212); text-align: center; border-bottom: 1px solid #333; }
    .hero h1 { font-size: 2.5rem; margin-bottom: 0.5rem; color: var(--primary); }
    .card { background: var(--surface); border-radius: 12px; padding: 1.5rem; margin: 1rem 0; border: 1px solid #333; }
</style>
<div class="hero">
    <h1 id="driver-name">Loading...</h1>
    <p id="web-tagline">Elite Private Driver Guide</p>
    <div style="display: flex; justify-content: center; gap: 10px; margin-top: 1rem;" id="web-services-list"></div>
</div>
<div class="container" style="padding: 2rem;">
    <h2 style="border-left: 4px solid var(--primary); padding-left: 1rem;">Experience Excellence</h2>
    <div class="card">
        <p id="driver-bio"></p>
        <p id="web-vehicle-info" style="font-weight: bold;"></p>
    </div>
    
    <h2 style="margin-top: 3rem;">Exploration Packages</h2>
    <div id="catalog-grid" style="display: grid; grid-template-columns: repeat(auto-fill, minmax(280px, 1fr)); gap: 20px;"></div>
</div>
""".trimIndent()
