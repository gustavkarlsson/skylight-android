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

    api("androidx.compose.ui:ui:${Versions.compose}")
    debugApi("androidx.compose.ui:ui-tooling:${Versions.compose}")
    api("androidx.compose.ui:ui-tooling-preview:${Versions.compose}")
    api("androidx.compose.compiler:compiler:${Versions.compose}")
    api("androidx.compose.foundation:foundation:${Versions.compose}")
    api("androidx.compose.material:material:${Versions.compose}")
    api("androidx.compose.material:material-icons-core:${Versions.compose}")
    api("androidx.compose.material:material-icons-extended:${Versions.compose}")
    api("androidx.compose.animation:animation:${Versions.compose}")
    api("androidx.constraintlayout:constraintlayout-compose:${Versions.composeConstraintLayout}")

    api("com.google.accompanist:accompanist-insets-ui:${Versions.accompanist}")
    api("com.google.accompanist:accompanist-systemuicontroller:${Versions.accompanist}")
}
