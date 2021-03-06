plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
}

android {
    commonConfig()
    composeConfig()
}

dependencies {
    implementation(project(":core"))
    implementation(project(":lib:ui-compose"))
    implementation(project(":lib:geocoder"))
    implementation(project(":lib:places"))
    implementation(project(":lib:location"))

    implementation("com.github.gustavkarlsson.conveyor:conveyor-core:${Versions.conveyor}")

    kapt("com.google.dagger:dagger-compiler:${Versions.dagger}")
}
