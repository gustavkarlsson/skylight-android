plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
}

android {
    commonConfig()
}

dependencies {
    implementation(project(":core"))
    implementation(project(":lib:permissions"))

    implementation("com.google.android.gms:play-services-location:${Versions.playServicesLocation}")
    implementation(
        "org.jetbrains.kotlinx:kotlinx-coroutines-play-services:${Versions.kotlinxCoroutines}",
    )

    kapt("com.google.dagger:dagger-compiler:${Versions.dagger}")
}
