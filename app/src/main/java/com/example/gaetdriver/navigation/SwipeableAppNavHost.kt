package com.example.gaetdriver.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.gaetdriver.constant.AppNavDestinations
import com.example.gaetdriver.core.firebase.AuthManager
import com.example.gaetdriver.features.home.HomeScreen
import com.example.gaetdriver.features.activity.ActivityScreen
import com.example.gaetdriver.features.library.LibraryScreen
import com.example.gaetdriver.features.profile.ProfileScreen

/**
 * A navigation host that supports swiping between top-level destinations.
 */
@Composable
fun SwipeableAppNavHost(
    authManager: AuthManager,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val tabs = remember {
        listOf(
            AppNavDestinations.HOME,
            AppNavDestinations.ACTIVITY,
            AppNavDestinations.LIBRARY,
            AppNavDestinations.PROFILE
        )
    }

    val pagerState = rememberPagerState(pageCount = { tabs.size })

    // 1. Sync Pager with NavController (When clicking Bottom Bar)
    LaunchedEffect(navController.currentBackStackEntryFlow) {
        navController.currentBackStackEntryFlow.collect { backStackEntry ->
            val route = backStackEntry.destination.route
            val targetPage = tabs.indexOfFirst { it.route == route }
            if (targetPage != -1 && pagerState.currentPage != targetPage) {
                pagerState.animateScrollToPage(targetPage)
            }
        }
    }

    // 2. Sync NavController with Pager (When Swiping)
    LaunchedEffect(pagerState.currentPage) {
        val targetRoute = tabs[pagerState.currentPage].route
        val currentRoute = navController.currentBackStackEntry?.destination?.route
        if (currentRoute != targetRoute) {
            // We use standard navigate. NavHost below ensures the graph is set.
            navController.navigate(targetRoute) {
                popUpTo(tabs[0].route) { saveState = true }
                launchSingleTop = true
                restoreState = true
            }
        }
    }

    // 3. The NavHost provides the graph and handles the routing state.
    // We use a single destination that contains the Pager to enable swiping.
    // However, to keep the Bottom Bar in sync with routes, we define all routes here.
    NavHost(
        navController = navController,
        startDestination = tabs[0].route,
        modifier = modifier.fillMaxSize()
    ) {
        tabs.forEach { tab ->
            composable(tab.route) {
                // The actual content is managed by the HorizontalPager globally
                // to maintain state and allow smooth finger-tracking swipes.
                // We show the pager on EVERY top-level destination.
                TabsPager(
                    pagerState = pagerState,
                    authManager = authManager,
                    tabs = tabs
                )
            }
        }
    }
}

@Composable
private fun TabsPager(
    pagerState: androidx.compose.foundation.pager.PagerState,
    authManager: AuthManager,
    tabs: List<AppNavDestinations>
) {
    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize(),
        beyondViewportPageCount = 1,
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
