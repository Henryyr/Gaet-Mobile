package com.example.gaetdriver.features.library

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.gaetdriver.core.base.i18n.LocalStrings
import com.example.gaetdriver.core.firebase.rememberAuthManager
import com.example.gaetdriver.core.data.repository.rememberPortfolioRepository
import com.example.gaetdriver.core.data.model.CatalogItem
import com.example.gaetdriver.core.ui.components.AppCard
import com.example.gaetdriver.core.ui.components.AppDialog
import com.example.gaetdriver.core.ui.components.AppImage
import com.example.gaetdriver.core.ui.components.EmptyState
import com.example.gaetdriver.core.ui.components.SectionHeader
import com.example.gaetdriver.core.ui.layout.ViewLayout
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen() {
    val strings = LocalStrings.current
    val portfolioRepo = rememberPortfolioRepository()
    val authManager = rememberAuthManager()
    val scope = rememberCoroutineScope()
    
    val userId = authManager.currentUserId
    val catalogItems by portfolioRepo.getCatalogItems(userId).collectAsState(initial = emptyList())
    
    // Using direct state objects to avoid "Assigned value is never read" delegate warnings
    val selectedItem = remember { mutableStateOf<CatalogItem?>(null) }
    val showActionSheet = remember { mutableStateOf(false) }
    val showDeleteDialog = remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    ViewLayout(
        header = {
            SectionHeader(title = strings.library)
        },
        body = {
            AppCard {
                if (catalogItems.isEmpty()) {
                    EmptyState(
                        message = strings.libraryEmpty,
                        description = strings.libraryDescription
                    )
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        contentPadding = PaddingValues(10.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(catalogItems) { item ->
                            AppImage(
                                model = item.imageBase64,
                                modifier = Modifier
                                    .aspectRatio(1f)
                                    .fillMaxSize(),
                                onClick = {
                                    selectedItem.value = item
                                    showActionSheet.value = true
                                }
                            )
                        }
                    }
                }
            }
        }
    )

    if (showActionSheet.value && selectedItem.value != null) {
        ModalBottomSheet(
            onDismissRequest = { 
                showActionSheet.value = false 
                selectedItem.value = null
            },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp)
            ) {
                ListItem(
                    headlineContent = { Text(strings.edit) },
                    leadingContent = { Icon(Icons.Default.Edit, contentDescription = null) },
                    modifier = Modifier.clickable {
                        showActionSheet.value = false
                    }
                )
                ListItem(
                    headlineContent = { Text(strings.delete) },
                    leadingContent = { Icon(Icons.Default.Delete, contentDescription = null) },
                    modifier = Modifier.clickable {
                        showActionSheet.value = false
                        showDeleteDialog.value = true
                    }
                )
            }
        }
    }

    if (showDeleteDialog.value && selectedItem.value != null) {
        AppDialog(
            title = strings.confirmDelete,
            message = strings.deleteMessage,
            confirmLabel = strings.delete,
            onConfirm = {
                selectedItem.value?.let { item ->
                    scope.launch {
                        portfolioRepo.deleteCatalogItem(item.id)
                    }
                }
                showDeleteDialog.value = false
                selectedItem.value = null
            },
            dismissLabel = strings.cancel,
            onDismiss = {
                showDeleteDialog.value = false
                selectedItem.value = null
            }
        )
    }
}
