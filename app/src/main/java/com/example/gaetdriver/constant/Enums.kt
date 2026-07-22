package com.example.gaetdriver.constant

import com.example.gaetdriver.R

enum class AppNavDestinations(
    val route: String,
    val icon: Int,
    val label: String
) {
    HOME(route = "home", icon = R.drawable.ic_home, label = "Home"),
    PREVIEW(route = "preview", icon = R.drawable.ic_preview, label = "Web"),
    ADD(route = "add", icon = R.drawable.ic_add, label = "Add"),
    LIBRARY(route = "library", icon = R.drawable.ic_library, label = "Library"),
    PROFILE(route = "profile", icon = R.drawable.ic_account_box, label = "Profile")
}

enum class Variant {
    Elevated, Outlined, Flat
}
