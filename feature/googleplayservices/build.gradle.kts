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
    namespace = "se.gustavkarlsson.skylight.android.feature.googleplayservices"
}

dependencies {
    implementation(project(":core"))
    implementation(project(":lib:ui-compose"))

    implementation(libs.play.services.gcm)
    implementation(libs.kotlinx.coroutines.play.services)
    implementation(libs.conveyor.core)

    ksp(libs.kotlin.inject.compiler)
}
