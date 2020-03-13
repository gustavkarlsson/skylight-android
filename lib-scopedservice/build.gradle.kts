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

    kapt("com.google.dagger:dagger-compiler:${Versions.dagger}")
}
