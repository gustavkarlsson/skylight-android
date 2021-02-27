plugins {
    id("com.android.library")
    id("kotlin-android")
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
    implementation(project(":lib:analytics"))

    api("androidx.compose.ui:ui:${Versions.compose}")
    api("androidx.compose.ui:ui-tooling:${Versions.compose}")
    api("androidx.compose.foundation:foundation:${Versions.compose}")
    api("androidx.compose.material:material:${Versions.compose}")
    api("androidx.compose.material:material-icons-core:${Versions.compose}")
    api("androidx.compose.material:material-icons-extended:${Versions.compose}")
}
