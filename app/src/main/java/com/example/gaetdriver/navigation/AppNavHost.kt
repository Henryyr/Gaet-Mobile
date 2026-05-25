package com.example.gaetdriver.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.gaetdriver.constant.AppNavDestinations
import com.example.gaetdriver.core.firebase.AuthManager
import com.example.gaetdriver.features.home.HomeScreen
import com.example.gaetdriver.features.activity.ActivityScreen
import com.example.gaetdriver.features.library.LibraryScreen
import com.example.gaetdriver.features.profile.ProfileScreen

/**
 * Main Navigation Host that uses a Pager for smooth, stutter-free transitions.
 * This replaces the standard NavHost for top-level destinations to enable swiping.
 */
@Composable
fun AppNavHost(
    authManager: AuthManager,
    pagerState: PagerState,
    modifier: Modifier = Modifier
) {
    val tabs = listOf(
        AppNavDestinations.HOME,
        AppNavDestinations.ACTIVITY,
        AppNavDestinations.LIBRARY,
        AppNavDestinations.PROFILE
    )

    HorizontalPager(
        state = pagerState,
        modifier = modifier.fillMaxSize(),
        beyondViewportPageCount = tabs.size,
        userScrollEnabled = true
    ) { page ->
        when (tabs[page]) {
            AppNavDestinations.HOME -> HomeScreen()
            AppNavDestinations.ACTIVITY -> ActivityScreen()
            AppNavDestinations.LIBRARY -> LibraryScreen()
            AppNavDestinations.PROFILE -> ProfileScreen(authManager = authManager)
            else -> {}
        }
    }
}
