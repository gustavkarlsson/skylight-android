plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
}

android {
    commonConfig()
}

dependencies {
    implementation(project(":core"))
    implementation(project(":lib:location"))

    implementation("com.dropbox.mobile.store:store4:${Versions.store}")

    kapt("com.google.dagger:dagger-compiler:${Versions.dagger}")
}
