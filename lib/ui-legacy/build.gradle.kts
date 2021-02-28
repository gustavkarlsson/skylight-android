plugins {
    id("com.android.library")
    id("kotlin-android")
}

android {
    commonConfig()
}

dependencies {
    implementation(project(":core"))
    implementation(project(":lib:analytics"))
    api(project(":lib:ui"))

    api("com.google.android.material:material:${Versions.material}")
    api("androidx.constraintlayout:constraintlayout:${Versions.androidConstraintLayout}")

    // FlowBinding
    api("io.github.reactivecircus.flowbinding:flowbinding-android:${Versions.flowbinding}")
    api("io.github.reactivecircus.flowbinding:flowbinding-appcompat:${Versions.flowbinding}")

    api("de.halfbit:edge-to-edge:${Versions.edgeToEdge}")
}
