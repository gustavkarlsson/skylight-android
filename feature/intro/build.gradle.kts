plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
    id("kotlin-parcelize")
}

android {
    commonConfig()
    composeConfig()
}

dependencies {
    implementation(project(":core"))
    implementation(project(":lib:ui-compose"))
    implementation(project(":lib:runversion"))
    implementation(project(":lib:permissions"))

    kapt("com.google.dagger:dagger-compiler:${Versions.dagger}")
}
