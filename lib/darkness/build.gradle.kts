plugins {
    id("com.android.library")
    kotlin("android")
    alias(libs.plugins.ksp)
}

android {
    commonConfig()
    namespace = "se.gustavkarlsson.skylight.android.lib.darkness"
}

dependencies {
    implementation(project(":core"))
    implementation(project(":lib:time"))
    implementation(project(":lib:location"))

    implementation(libs.solarpositioning)

    ksp(libs.kotlin.inject.compiler)

    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.assertk) {
        exclude("org.jetbrains.kotlin")
    }
}
