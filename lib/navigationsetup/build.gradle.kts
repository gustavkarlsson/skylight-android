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
    implementation(project(":lib:navigation"))

    implementation("androidx.lifecycle:lifecycle-livedata-ktx:${Versions.androidLifecycle}")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.androidLifecycle}")

    kapt("com.google.dagger:dagger-compiler:${Versions.dagger}")
}
