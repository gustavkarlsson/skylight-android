plugins {
    id("com.android.library")
    id("kotlin-android")
}

dependencies {
    implementation(project(":core"))
    api(project(":lib-scopedservice"))

    api("com.google.android.material:material:${Versions.androidMaterial}")
    api("androidx.constraintlayout:constraintlayout:${Versions.constraintLayout}")

    // Koin
    api("org.koin:koin-androidx-scope:${Versions.koin}")

    // Rx misc
    api("io.reactivex.rxjava2:rxandroid:${Versions.rxandroid}")

    // RxBinding
    api("com.jakewharton.rxbinding2:rxbinding-kotlin:${Versions.rxbinding}")
    api("com.jakewharton.rxbinding2:rxbinding-support-v4-kotlin:${Versions.rxbinding}")
    api("com.jakewharton.rxbinding2:rxbinding-appcompat-v7-kotlin:${Versions.rxbinding}")
    api("com.jakewharton.rxbinding2:rxbinding-design-kotlin:${Versions.rxbinding}")
    api("com.jakewharton.rxbinding2:rxbinding-recyclerview-v7-kotlin:${Versions.rxbinding}")
}
