plugins {
    id("com.android.library")
    kotlin("android")
    alias(libs.plugins.ksp)
}

android {
    commonConfig()
    namespace = "se.gustavkarlsson.skylight.android.lib.permissions"
}

dependencies {
    implementation(project(":core"))

    implementation(libs.assent.coroutines)

    ksp(libs.kotlin.inject.compiler)
}
