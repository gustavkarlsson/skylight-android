plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
    id("kotlin-android-extensions")
}

dependencies {
    implementation(project(":core"))
    implementation(project(":lib-ui"))
    implementation("de.halfbit:knot:${Versions.knot}")

    kapt("com.google.dagger:dagger-compiler:${Versions.dagger}")
}
