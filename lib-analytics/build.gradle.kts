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

    // Firebase
    implementation("com.google.firebase:firebase-core:${Versions.firebase}")

    kapt("com.google.dagger:dagger-compiler:${Versions.dagger}")
}
