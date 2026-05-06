package com.example.gaetdriver.features.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.gaetdriver.core.base.i18n.LocalStrings
import com.example.gaetdriver.core.firebase.rememberAuthManager
import com.example.gaetdriver.core.data.repository.rememberPortfolioRepository
import com.example.gaetdriver.core.ui.components.AppImage
import com.example.gaetdriver.core.ui.components.EmptyState
import com.example.gaetdriver.core.ui.components.SectionHeader
import com.example.gaetdriver.core.ui.layout.ViewLayout

@Composable
fun HomeScreen() {
    val strings = LocalStrings.current
    val portfolioRepo = rememberPortfolioRepository()
    val authManager = rememberAuthManager()
    
    val userId = authManager.currentUserId
    val catalogItems by portfolioRepo.getCatalogItems(userId).collectAsState(initial = emptyList())

    ViewLayout(
        header = {
            SectionHeader(
                title = strings.home,
            )
        },
        body = {
            if (catalogItems.isEmpty()) {
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
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(catalogItems) { item ->
                            AppImage(
                                model = item.imageBase64,
                                modifier = Modifier
                                    .aspectRatio(1.7f) // Rectangle wide display
                            )
                        }
                    }
                }
            }
        }
    )
}
