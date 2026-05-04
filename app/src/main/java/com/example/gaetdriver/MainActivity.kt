package com.example.gaetdriver

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.navigation.compose.rememberNavController
import com.example.gaetdriver.core.firebase.rememberAuthManager
import com.example.gaetdriver.core.ui.components.AddOptionsBottomSheet
import com.example.gaetdriver.core.ui.components.BottomBarNavigation
import com.example.gaetdriver.core.ui.theme.GaetDriverTheme
import com.example.gaetdriver.core.utils.DeviceManager
import com.example.gaetdriver.core.utils.rememberMediaManager
import com.example.gaetdriver.features.login.LoginScreen
import com.example.gaetdriver.navigation.AppNavHost
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.window.core.layout.WindowSizeClass
import com.example.gaetdriver.core.base.i18n.LocalStrings
import com.example.gaetdriver.core.base.i18n.rememberStrings

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
    val navController = rememberNavController()
    val adaptiveInfo = currentWindowAdaptiveInfo()
    val isExpanded = adaptiveInfo.windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND)

    val authManager = rememberAuthManager()
    val isLoggedIn by authManager.isLoggedIn.collectAsState()

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
            var showAddOptions by remember { mutableStateOf(false) }
            val sheetState = rememberModalBottomSheetState()

            val mediaManager = rememberMediaManager(
                onImageCaptured = { bitmap ->
                    Log.d("Camera", "Captured bitmap: $bitmap")
                },
                onImageSelected = { uri ->
                    Log.d("PhotoPicker", "Selected URI: $uri")
                }
            )

            if (!isLoggedIn) {
                LoginScreen(authManager = authManager)
            } else {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        if (!isExpanded) {
                            BottomBarNavigation(
                                navController = navController,
                                windowSizeClass = adaptiveInfo.windowSizeClass,
                                onAddClick = { showAddOptions = true }
                            )
                        }
                    }
                ) { innerPadding ->
                    Row(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                    ) {
                        AppNavHost(
                            authManager = authManager,
                            navController = navController,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    if (showAddOptions) {
                        AddOptionsBottomSheet(
                            sheetState = sheetState,
                            onDismissRequest = { showAddOptions = false },
                            onCameraClick = {
                                mediaManager.launchCamera()
                                showAddOptions = false
                            },
                            onGalleryClick = {
                                mediaManager.launchGallery()
                                showAddOptions = false
                            }
                        )
                    }
                }
            }
        }
    }
}
