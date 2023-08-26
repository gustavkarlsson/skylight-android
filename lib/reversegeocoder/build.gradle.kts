plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
    id("com.squareup.anvil")
}

android {
    commonConfig()
    namespace = "se.gustavkarlsson.skylight.android.lib.reversegeocoder"
}

dependencies {
    implementation(project(":core"))
    implementation(project(":lib:location"))

    implementation(libs.bundles.store)

    kapt(libs.dagger.compiler)
}
