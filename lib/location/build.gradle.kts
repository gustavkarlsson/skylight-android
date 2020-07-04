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

    implementation("com.google.android.gms:play-services-location:${Versions.playServicesLocation}")
    implementation("com.patloew.rxlocation:rxlocation:${Versions.rxLocation}")

    kapt("com.google.dagger:dagger-compiler:${Versions.dagger}")
}
