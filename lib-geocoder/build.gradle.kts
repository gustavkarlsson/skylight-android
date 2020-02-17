plugins {
    id("com.android.library")
    id("kotlin-android")
}

android {
    defaultConfig {
        val mapboxApiKey = findProperty("mapbox_api_key") ?: run {
            logger.error("WARNING: mapbox_api_key not set")
            "mapbox_api_key_not_set"
        }
        buildConfigField("String", "MAPBOX_API_KEY", "\"$mapboxApiKey\"")
    }
}

dependencies {
    implementation(project(":core"))

    implementation("com.mapbox.mapboxsdk:mapbox-sdk-services:${Versions.mapboxServices}")
}
