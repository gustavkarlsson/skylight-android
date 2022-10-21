plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
    id("com.squareup.anvil")
    id("kotlin-parcelize")
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

    kapt("com.google.dagger:dagger-compiler:${Versions.dagger}")
}
