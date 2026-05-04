package com.example.gaetdriver.features.library

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.gaetdriver.core.base.i18n.LocalStrings
import com.example.gaetdriver.core.ui.components.AppCard
import com.example.gaetdriver.core.ui.components.AppImage
import com.example.gaetdriver.core.ui.components.EmptyState
import com.example.gaetdriver.core.ui.components.SectionHeader
import com.example.gaetdriver.core.ui.layout.ViewLayout
import com.example.gaetdriver.core.utils.rememberLocalStorageManager
import java.io.File

@Composable
fun LibraryScreen() {
    val strings = LocalStrings.current
    val storageManager = rememberLocalStorageManager()
    
    var localFiles by remember { mutableStateOf<List<File>>(emptyList()) }
    val spanPattern = remember { listOf(1, 2, 2, 1, 1, 1, 1, 2, 1) }

    fun refreshFiles() {
        localFiles = storageManager.getAllFiles().sortedByDescending { it.lastModified() }
    }

    LaunchedEffect(Unit) {
        refreshFiles()
    }

    ViewLayout(
        header = {
            SectionHeader(title = strings.library)
        },
        body = {
            AppCard {
                if (localFiles.isEmpty()) {
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
                        itemsIndexed(
                            items = localFiles,
                            span = { index, _ -> 
                                GridItemSpan(spanPattern[index % spanPattern.size]) 
                            }
                        ) { index, file ->
                            val spanValue = spanPattern[index % spanPattern.size]

                            val ratio = if (spanValue == 2) 2f else 1f
                            
                            AppImage(
                                model = file,
                                modifier = Modifier
                                    .aspectRatio(ratio)
                                    .fillMaxSize()
                            )
                        }
                    }
                }
            }
        }
    )
}
