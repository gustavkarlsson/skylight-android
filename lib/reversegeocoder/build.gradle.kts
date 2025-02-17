plugins {
    id("com.android.library")
    kotlin("android")
    alias(libs.plugins.ksp)
}

android {
    commonConfig()
    namespace = "se.gustavkarlsson.skylight.android.lib.reversegeocoder"
}

dependencies {
    implementation(project(":core"))
    implementation(project(":lib:location"))

    implementation(libs.bundles.store)

    ksp(libs.kotlin.inject.compiler)
}
