plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
    id("kotlin-parcelize")
}

android {
    commonConfig()
    composeConfig()
}

dependencies {
    implementation(project(":core"))
    implementation(project(":lib:ui-compose"))

    implementation("com.google.android.gms:play-services-gcm:${Versions.playServicesGcm}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:${Versions.kotlinxCoroutines}")
    implementation("com.github.gustavkarlsson.conveyor:conveyor-core:${Versions.conveyor}")

    kapt("com.google.dagger:dagger-compiler:${Versions.dagger}")
}
