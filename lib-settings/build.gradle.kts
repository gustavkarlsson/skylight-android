plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
    id("com.squareup.sqldelight")
}

dependencies {
    implementation(project(":core"))

    implementation("com.squareup.sqldelight:android-driver:${Versions.sqldelight}")
    implementation("com.squareup.sqldelight:rxjava2-extensions:${Versions.sqldelight}")

    kapt("com.google.dagger:dagger-compiler:${Versions.dagger}")
}
