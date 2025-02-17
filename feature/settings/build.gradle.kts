plugins {
    id("com.android.library")
    kotlin("android")
    alias(libs.plugins.ksp)
    kotlin("plugin.parcelize")
    alias(libs.plugins.compose.compiler)
}

android {
    commonConfig()
    composeConfig()

    setFlavorDimensions(listOf("environment"))
    namespace = "se.gustavkarlsson.skylight.android.feature.settings"
}

dependencies {
    implementation(project(":core"))
    implementation(project(":lib:ui-compose"))
    implementation(project(":lib:places"))
    implementation(project(":lib:settings"))

    ksp(libs.kotlin.inject.compiler)
}
