plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
    alias(libs.plugins.anvil)
}

android {
    commonConfig()
    namespace = "se.gustavkarlsson.skylight.android.lib.okhttp"
}

dependencies {
    implementation(project(":core"))

    api(libs.okhttp) // Dagger requires api

    kapt(libs.dagger.compiler)
}
