plugins {
    id("com.android.library")
    id("kotlin-android")
    kotlin("kapt")
}

android {
    commonConfig()
}

dependencies {
    implementation(project(":core"))
    api(project(":lib:navigation"))
    api(project(":lib:scopedservice"))

    // Lifecycle
    api("androidx.lifecycle:lifecycle-runtime-ktx:${Versions.androidLifecycle}")
    kapt("androidx.lifecycle:lifecycle-compiler:${Versions.androidLifecycle}")
}
