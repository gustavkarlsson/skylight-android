plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
    id("com.squareup.anvil")
}

android {
    commonConfig()
    namespace = "se.gustavkarlsson.skylight.android.lib.time"
}

dependencies {
    implementation(project(":core"))

    kapt(libs.dagger.compiler)
}
