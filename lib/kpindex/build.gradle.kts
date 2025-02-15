plugins {
    id("com.android.library")
    kotlin("android")
    alias(libs.plugins.ksp)
    kotlin("plugin.serialization")
}

android {
    commonConfig()
    namespace = "se.gustavkarlsson.skylight.android.lib.kpindex"
}

dependencies {
    implementation(project(":core"))
    implementation(project(":lib:time"))
    implementation(project(":lib:okhttp"))

    implementation(libs.bundles.retrofit)
    implementation(libs.bundles.store)

    ksp(libs.kotlin.inject.compiler)

    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.assertk) {
        exclude("org.jetbrains.kotlin")
    }
    testImplementation(libs.commonsio)
}
