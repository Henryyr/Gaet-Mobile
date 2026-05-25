package com.example.gaetdriver.features.home

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.gaetdriver.core.base.i18n.LocalStrings
import com.example.gaetdriver.core.firebase.rememberAuthManager
import com.example.gaetdriver.core.data.repository.rememberPortfolioRepository
import com.example.gaetdriver.core.data.model.CatalogItem
import com.example.gaetdriver.core.ui.components.AppCard
import com.example.gaetdriver.core.ui.components.AppImage
import com.example.gaetdriver.core.ui.components.EmptyState
import com.example.gaetdriver.core.ui.components.SectionHeader
import com.example.gaetdriver.core.ui.layout.ViewLayout
import java.util.Locale

@Composable
fun HomeScreen() {
    val strings = LocalStrings.current
    val context = LocalContext.current
    val portfolioRepo = rememberPortfolioRepository()
    val authManager = rememberAuthManager()
    
    val userId = authManager.currentUserId
    
    // Use initial = null to distinguish between "Loading" and "Empty"
    val catalogItems by portfolioRepo.getCatalogItems(userId).collectAsState(initial = null)
    
    val previewItem = remember { mutableStateOf<CatalogItem?>(null) }

    ViewLayout(
        header = {
            SectionHeader(
                title = strings.home,
                trailingContent = {
                    IconButton(
                        onClick = {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, "My Portfolio")
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    "Check out my driver portfolio: https://gaet-driver.web.app/portfolio/$userId"
                                )
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Share Portfolio"))
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        },
        body = {
            if (catalogItems == null) {
                // Show loading indicator or nothing (to prevent flicker)
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                }
            } else if (catalogItems!!.isEmpty()) {
                EmptyState(
                    message = strings.home,
                    description = strings.homeDescription
                )
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = strings.homeDescription,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(1),
                        contentPadding = PaddingValues(bottom = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(catalogItems!!) { item ->
                            CatalogCard(item = item) {
                                previewItem.value = item
                            }
                        }
                    }
                }
            }
        }
    )

    // Full-screen Image Preview
    if (previewItem.value != null) {
        Dialog(
            onDismissRequest = { previewItem.value = null },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                AppImage(
                    model = previewItem.value?.imageBase64,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit,
                    onClick = { previewItem.value = null }
                )
            }
        }
    }
}

@Composable
fun CatalogCard(item: CatalogItem, onClick: () -> Unit) {
    AppCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Box(modifier = Modifier.fillMaxWidth().aspectRatio(1.5f)) {
            AppImage(
                model = item.imageBase64,
                modifier = Modifier.fillMaxSize()
            )
            
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f)),
                            startY = 100f
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                Text(
                    text = if (item.title.isBlank()) "Unnamed Trip" else item.title,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                if (item.price > 0) {
                    val locale = LocalConfiguration.current.locales[0]
                    Text(
                        text = String.format(locale, "Rp %,.0f", item.price),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }
        }
    }
}
