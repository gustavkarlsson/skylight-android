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

    implementation("com.sagar:coroutinespermission:${Versions.coroutinespermission}")

    kapt("com.google.dagger:dagger-compiler:${Versions.dagger}")
}
