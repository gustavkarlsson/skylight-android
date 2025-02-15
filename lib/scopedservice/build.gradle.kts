plugins {
    id("com.android.library")
    kotlin("android")
    alias(libs.plugins.ksp)
}

android {
    commonConfig()
    namespace = "se.gustavkarlsson.skylight.android.lib.scopedservice"
}

dependencies {
    implementation(project(":core"))

    ksp(libs.kotlin.inject.compiler)
}
