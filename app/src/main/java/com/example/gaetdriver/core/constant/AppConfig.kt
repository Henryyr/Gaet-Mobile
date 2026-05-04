package com.example.gaetdriver.core.constant

import com.example.gaetdriver.BuildConfig

/**
 * Global configuration to access environment-specific variables.
 * This class uses values defined in build.gradle.kts flavors.
 */
object AppConfig {
    val baseUrl: String = BuildConfig.BASE_URL

    val isDev: Boolean = BuildConfig.FLAVOR == "dev"
    val isStaging: Boolean = BuildConfig.FLAVOR == "staging"
    val isProd: Boolean = BuildConfig.FLAVOR == "prod"

    /**
     * Helper to get environment name for logging or UI debugging.
     */
    val environmentName: String = BuildConfig.FLAVOR.uppercase()
}
