plugins {
    id("com.android.library")
    kotlin("android")
    id("kotlin-android-extensions")
}

dependencies {
    implementation(project(":core"))
}
