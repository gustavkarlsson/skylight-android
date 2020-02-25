plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
}

dependencies {
    implementation(project(":core"))

    kapt("com.google.dagger:dagger-compiler:${Versions.dagger}")
}
