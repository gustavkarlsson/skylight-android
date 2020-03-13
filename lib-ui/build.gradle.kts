plugins {
    id("com.android.library")
    id("kotlin-android")
}

android {
    commonConfig()
}

dependencies {
    implementation(project(":core"))
    api(project(":lib-scopedservice"))

    api("com.google.android.material:material:${Versions.material}")
    api("androidx.constraintlayout:constraintlayout:${Versions.androidConstraintLayout}")

    // Rx misc
    api("io.reactivex.rxjava2:rxandroid:${Versions.rxandroid}")

    // RxBinding
    api("com.jakewharton.rxbinding2:rxbinding-kotlin:${Versions.rxbinding}")
    api("com.jakewharton.rxbinding2:rxbinding-support-v4-kotlin:${Versions.rxbinding}")
    api("com.jakewharton.rxbinding2:rxbinding-appcompat-v7-kotlin:${Versions.rxbinding}")
    api("com.jakewharton.rxbinding2:rxbinding-design-kotlin:${Versions.rxbinding}")
    api("com.jakewharton.rxbinding2:rxbinding-recyclerview-v7-kotlin:${Versions.rxbinding}")

    api("de.halfbit:edge-to-edge:${Versions.edgeToEdge}")
}
