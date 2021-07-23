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
    implementation(project(":lib:scopedservice"))

    implementation("androidx.compose.runtime:runtime:${Versions.compose}")
}
