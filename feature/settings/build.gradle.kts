plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
    id("kotlin-parcelize")
}

android {
    commonConfig()
    composeConfig()

    flavorDimensions += "environment"
}

dependencies {
    implementation(project(":core"))
    implementation(project(":lib:ui-compose"))
    implementation(project(":lib:settings"))

    kapt("com.google.dagger:dagger-compiler:${Versions.dagger}")
}
