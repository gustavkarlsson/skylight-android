plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
}

dependencies {
    implementation(project(":core"))

    implementation("com.google.android.gms:play-services-location:${Versions.playServicesLocation}")
    implementation("com.patloew.rxlocation:rxlocation:${Versions.rxLocation}")

    // Dagger
    kapt("com.google.dagger:dagger-compiler:${Versions.dagger}")
}
