plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
    id("com.squareup.sqldelight")
}

android {
    commonConfig()
}

dependencies {
    implementation(project(":core"))
    implementation(project(":lib:analytics"))
    implementation(project(":lib:location"))

    implementation("com.squareup.sqldelight:android-driver:${Versions.sqldelight}")
    implementation("com.squareup.sqldelight:coroutines-extensions:${Versions.sqldelight}")
    implementation("com.github.gustavkarlsson.conveyor:conveyor-core:${Versions.conveyor}")

    kapt("com.google.dagger:dagger-compiler:${Versions.dagger}")
}
