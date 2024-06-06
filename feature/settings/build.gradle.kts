plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
    alias(libs.plugins.anvil)
    kotlin("plugin.parcelize")
}

android {
    commonConfig()
    composeConfig()

    setFlavorDimensions(listOf("environment"))
    namespace = "se.gustavkarlsson.skylight.android.feature.settings"
}

dependencies {
    implementation(project(":core"))
    implementation(project(":lib:ui-compose"))
    implementation(project(":lib:settings"))

    kapt(libs.dagger.compiler)
}
