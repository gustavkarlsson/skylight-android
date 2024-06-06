plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
    alias(libs.plugins.anvil)
}

android {
    commonConfig()
    namespace = "se.gustavkarlsson.skylight.android.lib.scopedservice"
}

dependencies {
    implementation(project(":core"))

    kapt(libs.dagger.compiler)
}
