plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
    alias(libs.plugins.anvil)
}

android {
    commonConfig()
    namespace = "se.gustavkarlsson.skylight.android.lib.location"
}

dependencies {
    implementation(project(":core"))
    implementation(project(":lib:permissions"))

    implementation(libs.play.services.location)
    implementation(libs.kotlinx.coroutines.play.services)

    kapt(libs.dagger.compiler)
}
