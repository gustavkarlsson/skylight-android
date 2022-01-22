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

    // Firebase
    implementation("com.google.firebase:firebase-analytics-ktx:${Versions.firebaseAnalytics}")

    kapt("com.google.dagger:dagger-compiler:${Versions.dagger}")
}
