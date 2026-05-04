package com.example.gaetdriver.constant

import com.example.gaetdriver.R

enum class AppNavDestinations(
    val route: String,
    val icon: Int,
    val label: String
) {
    HOME(route = "home", icon = R.drawable.ic_home, label = "Home"),
    ACTIVITY(route = "activity", icon = R.drawable.ic_activity, label = "Activity"),
    ADD(route = "add", icon = R.drawable.ic_add, label = "Add"),
    LIBRARY(route = "library", icon = R.drawable.ic_library, label = "Library"),
    PROFILE(route = "profile", icon = R.drawable.ic_account_box, label = "Profile")
}
