// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    id("com.google.gms.google-services") version "4.4.4" apply false
}

// Global Seeder Task - Delegates to the :admin-tools module
// This task will appear under the 'gaet' group in the Gradle panel.
tasks.register("seedDatabase") {
    group = "gaet"
    description = "Runs the Firebase seeder and user reset utility from the admin module."
    
    dependsOn(":admin-tools:run")
}