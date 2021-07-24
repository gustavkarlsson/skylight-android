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
    implementation(project(":lib:scopedservice"))

    implementation("androidx.compose.runtime:runtime:${Versions.compose}")

    kapt("com.google.dagger:dagger-compiler:${Versions.dagger}")
}
