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

    // Firebase
    implementation("com.google.firebase:firebase-analytics-ktx:${Versions.firebaseAnalytics}")

    kapt("com.google.dagger:dagger-compiler:${Versions.dagger}")
}
