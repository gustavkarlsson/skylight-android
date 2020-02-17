plugins {
    id("com.android.library")
    id("kotlin-android")
}

dependencies {
    implementation(project(":core"))

    implementation("com.github.tbruyelle:rxpermissions:${Versions.rxpermissions}")
    implementation("androidx.appcompat:appcompat:${Versions.androidAppcompat}")
}
