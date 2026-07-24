package com.example.gaetdriver.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.gaetdriver.constant.AppNavDestinations
import com.example.gaetdriver.core.firebase.AuthManager
import com.example.gaetdriver.features.home.HomeScreen
import com.example.gaetdriver.features.webview.WebViewScreen
import com.example.gaetdriver.features.library.LibraryScreen
import com.example.gaetdriver.features.profile.ProfileScreen
import com.example.gaetdriver.core.utils.DeviceManager

/**
 * Main Navigation Host that uses a Pager for smooth, stutter-free transitions.
 */
@Composable
fun AppNavHost(
    authManager: AuthManager,
    pagerState: PagerState,
    onNavigateFullScreen: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val deviceManager = remember { DeviceManager(context) }
    
    // Global setting from Profile
    val isSwipeNavEnabled by deviceManager.isSwipeNavEnabled.collectAsState(initial = true)
    
    val tabs = listOf(
        AppNavDestinations.HOME,
        AppNavDestinations.PREVIEW,
        AppNavDestinations.LIBRARY,
        AppNavDestinations.PROFILE
    )

    HorizontalPager(
        state = pagerState,
        modifier = modifier.fillMaxSize(),
        beyondViewportPageCount = tabs.size,
        userScrollEnabled = isSwipeNavEnabled 
    ) { page ->
        when (tabs[page]) {
            AppNavDestinations.HOME -> HomeScreen()
            AppNavDestinations.PREVIEW -> WebViewScreen(onNavigateFullScreen = onNavigateFullScreen)
            AppNavDestinations.LIBRARY -> LibraryScreen()
            AppNavDestinations.PROFILE -> ProfileScreen(authManager = authManager)
            else -> {}
        }
    }
}
