package com.example.gaetdriver

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import com.example.gaetdriver.core.base.AppException
import com.example.gaetdriver.core.base.i18n.LocalStrings
import com.example.gaetdriver.core.base.i18n.rememberStrings
import com.example.gaetdriver.core.data.model.CatalogItem
import com.example.gaetdriver.core.data.repository.rememberPortfolioRepository
import com.example.gaetdriver.core.firebase.rememberAuthManager
import com.example.gaetdriver.core.ui.components.*
import com.example.gaetdriver.core.ui.theme.GaetDriverTheme
import com.example.gaetdriver.core.utils.DeviceManager
import com.example.gaetdriver.core.utils.ImageUtils
import com.example.gaetdriver.core.utils.rememberMediaManager
import com.example.gaetdriver.features.login.LoginScreen
import com.example.gaetdriver.navigation.AppNavHost
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GaetDriverApp()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@PreviewScreenSizes
@Composable
fun GaetDriverApp() {
    val strings = rememberStrings()
    val adaptiveInfo = currentWindowAdaptiveInfo()
    val isExpanded = adaptiveInfo.windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND)

    val authManager = rememberAuthManager()
    val isLoggedIn by authManager.isLoggedIn.collectAsState()
    val isValidating by authManager.isValidating.collectAsState()

    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            authManager.validateSession()
        }
    }

    val context = LocalContext.current
    val deviceManager = remember { DeviceManager(context) }
    val themeMode by deviceManager.themeMode.collectAsState(initial = "system")

    val darkTheme = when (themeMode) {
        "light" -> false
        "dark" -> true
        else -> isSystemInDarkTheme()
    }

    CompositionLocalProvider(LocalStrings provides strings) {
        GaetDriverTheme(darkTheme = darkTheme) {
            if (isValidating) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                val showAddOptions = remember { mutableStateOf(false) }
                val showDetailsDialog = remember { mutableStateOf(false) }
                val pendingImage = remember { mutableStateOf<Bitmap?>(null) }
                val isUploading = remember { mutableStateOf(false) }

                // PagerState is now the SINGLE Source of Truth for navigation
                val pagerState = rememberPagerState(pageCount = { 4 })

                val portfolioRepo = rememberPortfolioRepository()
                val scope = rememberCoroutineScope()

                val mediaManager = rememberMediaManager(
                    onImageCaptured = { uri ->
                        uri?.let { u ->
                            scope.launch {
                                val uid = authManager.currentUserId ?: return@launch
                                try {
                                    val count = portfolioRepo.getUserItemCount(uid)
                                    if (count >= 6) {
                                        Toast.makeText(context, "Limit reached!", Toast.LENGTH_SHORT).show()
                                        return@launch
                                    }
                                } catch (e: Exception) {
                                    val appException = AppException.from(e)
                                    Toast.makeText(context, appException.errorMessage, Toast.LENGTH_SHORT).show()
                                    return@launch
                                }

                                val bitmap = withContext(Dispatchers.IO) {
                                    try {
                                        val input = context.contentResolver.openInputStream(u)
                                        BitmapFactory.decodeStream(input)
                                    } catch (_: Exception) {
                                        null
                                    }
                                }

                                bitmap?.let { b ->
                                    pendingImage.value = b
                                    showDetailsDialog.value = true
                                }
                            }
                        }
                    },
                    onImageSelected = { uri ->
                        uri?.let { u ->
                            scope.launch {
                                val uid = authManager.currentUserId ?: return@launch
                                try {
                                    val count = portfolioRepo.getUserItemCount(uid)
                                    if (count >= 6) {
                                        Toast.makeText(context, "Limit reached!", Toast.LENGTH_SHORT).show()
                                        return@launch
                                    }
                                } catch (e: Exception) {
                                    val appException = AppException.from(e)
                                    Toast.makeText(context, appException.errorMessage, Toast.LENGTH_SHORT).show()
                                    return@launch
                                }

                                val bitmap = withContext(Dispatchers.IO) {
                                    try {
                                        val input = context.contentResolver.openInputStream(u)
                                        BitmapFactory.decodeStream(input)
                                    } catch (_: Exception) {
                                        null
                                    }
                                }

                                bitmap?.let { b ->
                                    pendingImage.value = b
                                    showDetailsDialog.value = true
                                }
                            }
                        }
                    }
                )

                if (!isLoggedIn) {
                    LoginScreen(authManager = authManager)
                } else {
                    Row(modifier = Modifier.fillMaxSize()) {
                        if (isExpanded) {
                            BottomBarNavigation(
                                pagerState = pagerState,
                                windowSizeClass = adaptiveInfo.windowSizeClass,
                                onAddClick = { showAddOptions.value = true }
                            )
                        }

                        Scaffold(
                            modifier = Modifier.weight(1f).fillMaxHeight(),
                            bottomBar = {
                                if (!isExpanded) {
                                    BottomBarNavigation(
                                        pagerState = pagerState,
                                        windowSizeClass = adaptiveInfo.windowSizeClass,
                                        onAddClick = { showAddOptions.value = true }
                                    )
                                }
                            }
                        ) { innerPadding ->
                            // AppNavHost now DIRECTLY hosts the Pager without standard NavHost
                            // ensuring 100% smooth transitions without jumping.
                            AppNavHost(
                                authManager = authManager,
                                pagerState = pagerState,
                                modifier = Modifier
                                    .padding(innerPadding)
                                    .fillMaxSize()
                            )

                            if (showAddOptions.value) {
                                AddOptionsBottomSheet(
                                    sheetState = rememberModalBottomSheetState(),
                                    onDismissRequest = { showAddOptions.value = false },
                                    onCameraClick = {
                                        mediaManager.launchCamera()
                                        showAddOptions.value = false
                                    },
                                    onGalleryClick = {
                                        mediaManager.launchGallery()
                                        showAddOptions.value = false
                                    }
                                )
                            }

                            if (showDetailsDialog.value && pendingImage.value != null) {
                                var tripTitle by remember { mutableStateOf("") }
                                var tripPrice by remember { mutableStateOf("") }

                                AlertDialog(
                                    onDismissRequest = {
                                        if (!isUploading.value) {
                                            showDetailsDialog.value = false
                                            pendingImage.value = null
                                        }
                                    },
                                    title = { Text("Trip Details") },
                                    text = {
                                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                            AppTextField(
                                                value = tripTitle,
                                                onValueChange = { tripTitle = it },
                                                label = "Title (e.g. Bali Tour)",
                                                enabled = !isUploading.value
                                            )
                                            AppTextField(
                                                value = tripPrice,
                                                onValueChange = { tripPrice = it },
                                                label = "Price (e.g. 500000)",
                                                enabled = !isUploading.value
                                            )
                                        }
                                    },
                                    confirmButton = {
                                        AppButton(
                                            text = "Save",
                                            isLoading = isUploading.value,
                                            onClick = {
                                                val bitmap = pendingImage.value ?: return@AppButton
                                                isUploading.value = true
                                                scope.launch {
                                                    try {
                                                        val uid = authManager.currentUserId ?: return@launch
                                                        val base64 = withContext(Dispatchers.Default) {
                                                            ImageUtils.encodeToBase64(bitmap)
                                                        }
                                                        portfolioRepo.addCatalogItem(
                                                            CatalogItem(
                                                                userId = uid,
                                                                title = tripTitle,
                                                                price = tripPrice.toDoubleOrNull() ?: 0.0,
                                                                imageBase64 = base64
                                                            )
                                                        )
                                                        portfolioRepo.logActivity(
                                                            userId = uid,
                                                            type = "UPLOAD",
                                                            title = "Uploaded new Trip",
                                                            description = "Trip: $tripTitle"
                                                        )
                                                        // Instant jump to Library (Index 2) for performance
                                                        pagerState.scrollToPage(2)
                                                    } catch (e: Exception) {
                                                        val appException = AppException.from(e)
                                                        Toast.makeText(context, "Failed to save: ${appException.errorMessage}", Toast.LENGTH_LONG).show()
                                                    } finally {
                                                        isUploading.value = false
                                                        showDetailsDialog.value = false
                                                        pendingImage.value = null
                                                    }
                                                }
                                            }
                                        )
                                    },
                                    dismissButton = {
                                        if (!isUploading.value) {
                                            TextButton(onClick = {
                                                showDetailsDialog.value = false
                                                pendingImage.value = null
                                            }) {
                                                Text("Cancel")
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
