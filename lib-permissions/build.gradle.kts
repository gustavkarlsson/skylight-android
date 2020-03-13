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

    implementation("com.github.tbruyelle:rxpermissions:${Versions.rxpermissions}")

    kapt("com.google.dagger:dagger-compiler:${Versions.dagger}")
}
