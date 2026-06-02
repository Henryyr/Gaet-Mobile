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
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val deviceManager = remember { DeviceManager(context) }
    
    // Global setting from Profile
    val isSwipeNavEnabled by deviceManager.isSwipeNavEnabled.collectAsState(initial = true)
    
    val tabs = listOf(
        AppNavDestinations.HOME,
        AppNavDestinations.ACTIVITY,
        AppNavDestinations.LIBRARY,
        AppNavDestinations.PROFILE
    )

    // Current tab detection (Tab 1 is Activity which shows Web Preview)
    val isWebViewPage = tabs[pagerState.currentPage] == AppNavDestinations.ACTIVITY

    HorizontalPager(
        state = pagerState,
        modifier = modifier.fillMaxSize(),
        beyondViewportPageCount = tabs.size,
        // userScrollEnabled is true ONLY IF global setting is ON AND we aren't on the web page
        userScrollEnabled = isSwipeNavEnabled && !isWebViewPage 
    ) { page ->
        when (tabs[page]) {
            AppNavDestinations.HOME -> HomeScreen()
            AppNavDestinations.ACTIVITY -> WebViewScreen()
            AppNavDestinations.LIBRARY -> LibraryScreen()
            AppNavDestinations.PROFILE -> ProfileScreen(authManager = authManager)
            else -> {}
        }
    }
}
