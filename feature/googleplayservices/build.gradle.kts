plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
    id("kotlin-android-extensions")
}

android {
    commonConfig()
}

dependencies {
    implementation(project(":core"))
    implementation(project(":lib:ui"))

    implementation("com.google.android.gms:play-services-gcm:${Versions.playServicesGcm}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:${Versions.kotlinxCoroutines}")

    kapt("com.google.dagger:dagger-compiler:${Versions.dagger}")
}
