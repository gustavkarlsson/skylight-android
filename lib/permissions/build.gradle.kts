plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
    id("com.squareup.anvil")
}

android {
    commonConfig()
}

dependencies {
    implementation(project(":core"))

    implementation("com.github.florent37:runtime-permission-kotlin:${Versions.runtimePermissionKotlin}")

    kapt("com.google.dagger:dagger-compiler:${Versions.dagger}")
}
