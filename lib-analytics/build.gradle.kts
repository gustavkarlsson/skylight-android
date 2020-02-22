plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
}

dependencies {
    implementation(project(":core"))

    // Firebase
    implementation("com.google.firebase:firebase-core:${Versions.firebase}")

    // Dagger
    kapt("com.google.dagger:dagger-compiler:${Versions.dagger}")
}
