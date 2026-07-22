package com.example.gaetdriver.features.onboarding.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.gaetdriver.R
import com.example.gaetdriver.core.ui.components.AppImage

@Composable
fun OnboardingHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AppImage(
            model = R.drawable.gaet_logo,
            transparent = true,
            modifier = Modifier.size(100.dp),
            contentScale = ContentScale.Fit,
            contentDescription = "GAET Logo"
        )

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
    }
}
