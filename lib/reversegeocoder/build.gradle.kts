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

    implementation("com.dropbox.mobile.store:store4:${Versions.store}")

    kapt("com.google.dagger:dagger-compiler:${Versions.dagger}")
}
