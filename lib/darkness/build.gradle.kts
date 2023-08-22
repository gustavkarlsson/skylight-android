plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
    id("com.squareup.anvil")
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

    kapt(libs.dagger.compiler)

    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.mockito.inline)
    testImplementation(libs.mockito.kotlin) {
        exclude("org.jetbrains.kotlin")
    }
    testImplementation(libs.assertk) {
        exclude("org.jetbrains.kotlin")
    }
}
