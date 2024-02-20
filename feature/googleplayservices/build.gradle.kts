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
    namespace = "se.gustavkarlsson.skylight.android.feature.googleplayservices"
}

dependencies {
    implementation(project(":core"))
    implementation(project(":lib:ui-compose"))

    implementation(libs.play.services.gcm)
    implementation(libs.kotlinx.coroutines.play.services)
    implementation(libs.conveyor.core)

    kapt(libs.dagger.compiler)
}
