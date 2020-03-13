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

    implementation("net.e175.klaus:solarpositioning:${Versions.solarPositioning}")

    kapt("com.google.dagger:dagger-compiler:${Versions.dagger}")
}
