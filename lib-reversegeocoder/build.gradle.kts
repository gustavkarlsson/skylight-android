plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
}

dependencies {
    implementation(project(":core"))

    // Dagger
    kapt("com.google.dagger:dagger-compiler:${Versions.dagger}")
}
