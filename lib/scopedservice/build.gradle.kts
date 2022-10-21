plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
    id("com.squareup.anvil")
}

android {
    commonConfig()
    namespace = "se.gustavkarlsson.skylight.android.lib.scopedservice"
}

dependencies {
    implementation(project(":core"))

    kapt("com.google.dagger:dagger-compiler:${Versions.dagger}")
}
