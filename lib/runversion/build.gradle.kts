plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
    alias(libs.plugins.anvil)
}

android {
    commonConfig()
    namespace = "se.gustavkarlsson.skylight.android.lib.runversion"
}

dependencies {
    implementation(project(":core"))

    // DataStore
    implementation(libs.androidx.datastore.preferences)

    kapt(libs.dagger.compiler)

    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.assertk) {
        exclude("org.jetbrains.kotlin")
    }
}
