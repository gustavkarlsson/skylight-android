plugins {
    id("com.android.library")
    kotlin("android")
    alias(libs.plugins.ksp)
}

android {
    commonConfig()
    namespace = "se.gustavkarlsson.skylight.android.lib.time"
}

dependencies {
    implementation(project(":core"))

    ksp(libs.kotlin.inject.compiler)
}
