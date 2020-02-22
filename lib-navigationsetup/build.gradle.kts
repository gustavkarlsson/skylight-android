plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
    id("kotlin-android-extensions")
}

dependencies {
    implementation(project(":core"))
    implementation(project(":lib-ui"))
    implementation(project(":lib-navigation-newer"))

    implementation("com.github.Zhuinden:simple-stack:${Versions.simpleStack}")

    kapt("com.google.dagger:dagger-compiler:${Versions.dagger}")
}
