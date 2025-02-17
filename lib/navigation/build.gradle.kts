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
    namespace = "se.gustavkarlsson.skylight.android.lib.navigation"
}

dependencies {
    implementation(project(":core"))
    implementation(project(":lib:scopedservice"))

    implementation(libs.compose.runtime)

    ksp(libs.kotlin.inject.compiler)
}
