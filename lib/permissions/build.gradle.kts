plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
    id("com.squareup.anvil")
}

android {
    commonConfig()
}

dependencies {
    implementation(project(":core"))

    implementation("com.afollestad.assent:coroutines:${Versions.assent}")

    kapt("com.google.dagger:dagger-compiler:${Versions.dagger}")
}
