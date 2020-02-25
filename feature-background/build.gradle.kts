plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
}

dependencies {
    implementation(project(":core"))
    implementation(project(":lib-ui")) // TODO only used for coloring notification?

    // WorkManager
    implementation("androidx.work:work-runtime-ktx:${Versions.androidWorkManager}")
    implementation("androidx.work:work-rxjava2:${Versions.androidWorkManager}")

    kapt("com.google.dagger:dagger-compiler:${Versions.dagger}")
}
