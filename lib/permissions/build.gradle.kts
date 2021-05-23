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

    implementation("com.github.florent37:runtime-permission-kotlin:${Versions.runtimePermissionKotlin}")

    implementation("com.github.wowselim.goldfinch:goldfinch-annotation:${Versions.goldFinch}")
    kapt("com.github.wowselim.goldfinch:goldfinch-codegen:${Versions.goldFinch}")

    kapt("com.google.dagger:dagger-compiler:${Versions.dagger}")
}
