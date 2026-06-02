package com.example.gaetdriver.features.library

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.gaetdriver.core.base.i18n.LocalStrings
import com.example.gaetdriver.core.firebase.rememberAuthManager
import com.example.gaetdriver.core.data.repository.rememberPortfolioRepository
import com.example.gaetdriver.core.data.model.CatalogItem
import com.example.gaetdriver.core.ui.components.*
import com.example.gaetdriver.core.ui.layout.ViewLayout
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen() {
    val strings = LocalStrings.current
    val context = LocalContext.current
    val portfolioRepo = rememberPortfolioRepository()
    val authManager = rememberAuthManager()
    val scope = rememberCoroutineScope()
    
    val userId = authManager.currentUserId
    val catalogItems by portfolioRepo.getCatalogItems(userId).collectAsState(initial = emptyList())
    
    val selectedItem = remember { mutableStateOf<CatalogItem?>(null) }
    val showActionSheet = remember { mutableStateOf(false) }
    val showDeleteDialog = remember { mutableStateOf(false) }
    val showEditDialog = remember { mutableStateOf(false) }
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

    // Action Sheet
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
                        showEditDialog.value = true
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

    // Edit Dialog
    if (showEditDialog.value && selectedItem.value != null) {
        var tempTitle by remember { mutableStateOf(selectedItem.value?.title ?: "") }
        var tempPrice by remember { mutableStateOf(selectedItem.value?.price?.toString() ?: "0.0") }
        
        AlertDialog(
            onDismissRequest = { showEditDialog.value = false },
            title = { Text("Edit Trip Details") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    AppTextField(value = tempTitle, onValueChange = { tempTitle = it }, label = "Trip Title")
                    AppTextField(value = tempPrice, onValueChange = { tempPrice = it }, label = "Price (numeric)")
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val priceVal = tempPrice.toDoubleOrNull() ?: 0.0
                    selectedItem.value?.let { item ->
                        val updated = item.copy(title = tempTitle, price = priceVal)
                        scope.launch {
                            try {
                                portfolioRepo.addCatalogItem(updated)
                                portfolioRepo.logActivity(
                                    userId = userId ?: "",
                                    type = "EDIT",
                                    title = "Updated Trip",
                                    description = "Updated trip: ${updated.title}"
                                )
                                showEditDialog.value = false
                                selectedItem.value = null
                            } catch (e: Exception) {
                                Toast.makeText(context, "Update failed: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { 
                    showEditDialog.value = false
                    selectedItem.value = null
                }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Delete Dialog
    if (showDeleteDialog.value && selectedItem.value != null) {
        AppDialog(
            title = strings.confirmDelete,
            message = strings.deleteMessage,
            confirmLabel = strings.delete,
            onConfirm = {
                selectedItem.value?.let { item ->
                    scope.launch {
                        try {
                            portfolioRepo.deleteCatalogItem(item.id)
                            portfolioRepo.logActivity(
                                userId = userId ?: "",
                                type = "DELETE",
                                title = "Deleted Trip",
                                description = "Deleted trip: ${item.title}"
                            )
                        } catch (e: Exception) {
                            Toast.makeText(context, "Delete failed: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                        }
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
