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
    implementation(project(":lib:analytics"))
    api(project(":lib:navigation"))
    api(project(":lib:scopedservice"))

    api("com.google.android.material:material:${Versions.material}")
    api("androidx.constraintlayout:constraintlayout:${Versions.androidConstraintLayout}")

    // Lifecycle
    api("androidx.lifecycle:lifecycle-runtime-ktx:${Versions.androidLifecycle}")
    kapt("androidx.lifecycle:lifecycle-compiler:${Versions.androidLifecycle}")

    // RxBinding
    api("com.jakewharton.rxbinding3:rxbinding:${Versions.rxbinding}")
    api("com.jakewharton.rxbinding3:rxbinding-appcompat:${Versions.rxbinding}")

    // FlowBinding
    api("io.github.reactivecircus.flowbinding:flowbinding-android:${Versions.flowbinding}")
    api("io.github.reactivecircus.flowbinding:flowbinding-appcompat:${Versions.flowbinding}")

    api("de.halfbit:edge-to-edge:${Versions.edgeToEdge}")
}
