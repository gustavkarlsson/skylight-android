plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
    id("kotlin-android-extensions")
}

android {
    commonConfig()

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerVersion = Versions.kotlin
        kotlinCompilerExtensionVersion = Versions.compose
    }
}

dependencies {
    implementation(project(":core"))
    implementation(project(":lib:ui"))
    implementation(project(":lib:compose"))
    implementation(project(":lib:runversion"))
    implementation(project(":lib:permissions"))

    kapt("com.google.dagger:dagger-compiler:${Versions.dagger}")
}
