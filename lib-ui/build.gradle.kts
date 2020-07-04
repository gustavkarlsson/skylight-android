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
    implementation(project(":lib-analytics"))
    api(project(":lib-navigation"))
    api(project(":lib-scopedservice"))

    api("com.google.android.material:material:${Versions.material}")
    api("androidx.constraintlayout:constraintlayout:${Versions.androidConstraintLayout}")

    // Lifecycle
    api("androidx.lifecycle:lifecycle-runtime-ktx:${Versions.androidLifecycle}")
    kapt("androidx.lifecycle:lifecycle-compiler:${Versions.androidLifecycle}")

    // RxBinding
    api("com.jakewharton.rxbinding2:rxbinding-kotlin:${Versions.rxbinding}")
    api("com.jakewharton.rxbinding2:rxbinding-support-v4-kotlin:${Versions.rxbinding}")
    api("com.jakewharton.rxbinding2:rxbinding-appcompat-v7-kotlin:${Versions.rxbinding}")
    api("com.jakewharton.rxbinding2:rxbinding-design-kotlin:${Versions.rxbinding}")
    api("com.jakewharton.rxbinding2:rxbinding-recyclerview-v7-kotlin:${Versions.rxbinding}")

    api("de.halfbit:edge-to-edge:${Versions.edgeToEdge}")
}
