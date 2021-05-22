plugins {
    id("com.android.library")
    kotlin("android")
}

android {
    commonConfig()
    composeConfig()
}

dependencies {
    implementation(project(":core"))

    implementation("androidx.compose.runtime:runtime:${Versions.compose}")
}
