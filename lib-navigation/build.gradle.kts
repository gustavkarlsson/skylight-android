plugins {
    id("com.android.library")
    kotlin("android")
}

android {
    commonConfig()
}

dependencies {
    implementation(project(":core"))
}
