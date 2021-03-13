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
    implementation(project(":lib:ui"))

    implementation("com.github.Zhuinden:simple-stack:${Versions.simpleStack}")

    kapt("com.google.dagger:dagger-compiler:${Versions.dagger}")
}
