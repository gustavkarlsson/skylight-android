plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
    alias(libs.plugins.anvil)
    kotlin("plugin.serialization")
}

android {
    commonConfig()
    namespace = "se.gustavkarlsson.skylight.android.lib.kpindex"
}

dependencies {
    implementation(project(":core"))
    implementation(project(":lib:time"))

    implementation(libs.bundles.retrofit)
    implementation(libs.bundles.store)

    kapt(libs.dagger.compiler)

    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.assertk) {
        exclude("org.jetbrains.kotlin")
    }
    testImplementation(libs.commonsio)
}
