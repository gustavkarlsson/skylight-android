plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
}

android {
    defaultConfig {
        val mapboxApiKey = findProperty("mapbox_api_key") ?: run {
            logger.warn("WARNING: mapbox_api_key not set")
            "mapbox_api_key_not_set"
        }
        buildConfigField("String", "MAPBOX_API_KEY", "\"$mapboxApiKey\"")
    }
}

dependencies {
    implementation(project(":core"))

    implementation("com.mapbox.mapboxsdk:mapbox-sdk-services:${Versions.mapboxServices}")

    kapt("com.google.dagger:dagger-compiler:${Versions.dagger}")
}
