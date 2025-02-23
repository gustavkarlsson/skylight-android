plugins {
    id("com.android.library")
    kotlin("android")
    alias(libs.plugins.ksp)
}

android {
    commonConfig()
    namespace = "se.gustavkarlsson.skylight.android.lib.okhttp"
}

dependencies {
    implementation(project(":core"))

    api(libs.okhttp)

    ksp(libs.kotlin.inject.compiler)
}
