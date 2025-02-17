plugins {
    id("com.android.library")
    kotlin("android")
    alias(libs.plugins.compose.compiler)
}

android {
    commonConfig()
    composeConfig()
    namespace = "se.gustavkarlsson.skylight.android.lib.ui.compose"
}

dependencies {
    implementation(project(":core"))
    api(project(":lib:ui"))

    implementation(libs.markwon.core)

    api(libs.androidx.lifecycle.runtime.ktx)

    api(libs.compose.ui)
    debugApi(libs.compose.ui.tooling)
    api(libs.compose.ui.tooling.preview)
    api(libs.compose.foundation)
    api(libs.compose.material)
    api(libs.compose.material.icons.core)
    api(libs.compose.material.icons.extended)
    api(libs.compose.animation)
    api(libs.constraintlayout.compose)
}
