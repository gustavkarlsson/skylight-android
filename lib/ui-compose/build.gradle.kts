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
    implementation(project(":lib:analytics"))
    api(project(":lib:ui"))

    implementation("androidx.lifecycle:lifecycle-runtime-ktx:${Versions.androidLifecycle}")
    implementation("io.noties.markwon:core:${Versions.markwon}")

    api("androidx.compose.ui:ui:${Versions.compose}")
    api("androidx.compose.ui:ui-tooling-preview:${Versions.compose}")
    api("androidx.compose.compiler:compiler:${Versions.compose}")
    api("androidx.compose.foundation:foundation:${Versions.compose}")
    api("androidx.compose.material:material:${Versions.compose}")
    api("androidx.compose.material:material-icons-core:${Versions.compose}")
    api("androidx.compose.material:material-icons-extended:${Versions.compose}") // TODO do we need 'em?
    api("androidx.compose.animation:animation:${Versions.compose}")
    api("androidx.constraintlayout:constraintlayout-compose:${Versions.composeConstraintLayout}")

    api("com.google.accompanist:accompanist-insets:${Versions.accompanist}")
    api("com.google.accompanist:accompanist-insets-ui:${Versions.accompanist}")
    api("com.google.accompanist:accompanist-systemuicontroller:${Versions.accompanist}")
}
