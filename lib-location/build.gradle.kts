plugins {
    id("com.android.library")
    id("kotlin-android")
}

dependencies {
    implementation(project(":core"))

    implementation("com.google.android.gms:play-services-location:${Versions.playServicesLocation}")
    implementation("com.patloew.rxlocation:rxlocation:${Versions.rxLocation}")
}
