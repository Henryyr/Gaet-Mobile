package com.example.gaetdriver.navigation

import androidx.compose.runtime.Composable
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

@Composable
fun AppNavHost(
    authManager: AuthManager,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = AppNavDestinations.HOME.route,
        modifier = modifier
    ) {
        composable(AppNavDestinations.HOME.route) {
            HomeScreen()
        }

        composable(AppNavDestinations.ACTIVITY.route) {
            ActivityScreen()
        }


        composable(AppNavDestinations.LIBRARY.route) {
            LibraryScreen()
        }

        composable(AppNavDestinations.PROFILE.route) {
            ProfileScreen(authManager = authManager)
        }
    }
}
