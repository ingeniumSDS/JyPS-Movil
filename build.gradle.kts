// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.ksp) apply false // Plugin necesario para el Proyecto
    // Add the dependency for the Google services Gradle plugin
    alias(libs.plugins.googleServices) apply false
    alias(libs.plugins.crashlytics) apply false // Add the Crashlytics Gradle plugin
}