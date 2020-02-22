plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
}

dependencies {
    implementation(project(":core"))

    api("com.squareup.okhttp3:okhttp:${Versions.okHttp}") // Dagger requires it

    // Dagger
    kapt("com.google.dagger:dagger-compiler:${Versions.dagger}")
}
