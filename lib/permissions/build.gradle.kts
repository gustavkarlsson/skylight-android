plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
    id("com.squareup.anvil")
}

android {
    commonConfig()
    namespace = "se.gustavkarlsson.skylight.android.lib.permissions"
}

dependencies {
    implementation(project(":core"))

    implementation(libs.assent.coroutines)

    kapt(libs.dagger.compiler)
}
