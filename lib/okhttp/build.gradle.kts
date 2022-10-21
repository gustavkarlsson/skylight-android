plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
    id("com.squareup.anvil")
}

android {
    commonConfig()
    namespace = "se.gustavkarlsson.skylight.android.lib.okhttp"
}

dependencies {
    implementation(project(":core"))

    api("com.squareup.okhttp3:okhttp:${Versions.okHttp}") // Dagger requires api

    kapt("com.google.dagger:dagger-compiler:${Versions.dagger}")
}
