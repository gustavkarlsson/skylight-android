plugins {
    id("com.android.library")
    kotlin("android")
    alias(libs.plugins.ksp)
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

    ksp(libs.kotlin.inject.compiler)
}
