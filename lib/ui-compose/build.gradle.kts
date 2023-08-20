plugins {
    id("com.android.library")
    id("kotlin-android")
}

android {
    commonConfig()
    composeConfig()
    namespace = "se.gustavkarlsson.skylight.android.lib.ui.compose"
}

dependencies {
    implementation(project(":core"))
    api(project(":lib:ui"))

    implementation("io.noties.markwon:core:${Versions.markwon}")

    api("androidx.lifecycle:lifecycle-runtime-ktx:${Versions.androidLifecycle}")

    api("androidx.compose.ui:ui")
    debugApi("androidx.compose.ui:ui-tooling")
    api("androidx.compose.ui:ui-tooling-preview")
    api("androidx.compose.foundation:foundation")
    api("androidx.compose.material:material")
    api("androidx.compose.material:material-icons-core")
    api("androidx.compose.material:material-icons-extended")
    api("androidx.compose.animation:animation")
    api("androidx.constraintlayout:constraintlayout-compose:${Versions.composeConstraintLayout}")

    api("com.google.accompanist:accompanist-insets-ui:${Versions.accompanist}")
    api("com.google.accompanist:accompanist-systemuicontroller:${Versions.accompanist}")
}
