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
    namespace = "se.gustavkarlsson.skylight.android.lib.navigation"
}

dependencies {
    implementation(project(":core"))
    implementation(project(":lib:scopedservice"))

    implementation(libs.compose.runtime)

    kapt(libs.dagger.compiler)
}
