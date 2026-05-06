package com.example.gaetdriver.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.gaetdriver.core.utils.ImageUtils

/**
 * A standardized way to display images with placeholders.
 */
@Composable
fun AppImage(
    model: Any?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    contentDescription: String? = null,
    onClick: (() -> Unit)? = null
) {
    // Decode Base64 if needed
    val displayModel = remember(model) {
        if (model is String && model.length > 100) { // Simple heuristic for Base64
            ImageUtils.decodeFromBase64(model)
        } else {
            model
        }
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .then(
                if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier
            ),
        contentAlignment = Alignment.Center,
    ) {
        if (displayModel == null) {
            Icon(
                imageVector = Icons.Default.Image,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                modifier = Modifier.fillMaxSize(0.4f)
            )
        } else {
            AsyncImage(
                model = displayModel,
                contentDescription = contentDescription,
                contentScale = contentScale,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
