plugins {
    id("com.android.library")
    id("kotlin-android")
}

android {
    commonConfig()
    composeConfig()
}

dependencies {
    implementation(project(":core"))
    implementation(project(":lib:ui"))
    implementation(project(":lib:analytics"))

    api("androidx.compose.ui:ui:${Versions.compose}")
    api("androidx.compose.ui:ui-tooling:${Versions.compose}")
    api("androidx.compose.compiler:compiler:${Versions.compose}")
    api("androidx.compose.foundation:foundation:${Versions.compose}")
    api("androidx.compose.material:material:${Versions.compose}")
    api("androidx.compose.material:material-icons-core:${Versions.compose}")
    api("androidx.compose.material:material-icons-extended:${Versions.compose}")

    api("dev.chrisbanes.accompanist:accompanist-insets:${Versions.accompanist}")
    api("com.google.android.material:compose-theme-adapter:${Versions.composeThemeAdapter}")
}
