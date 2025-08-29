plugins {
    id("com.android.library")
    kotlin("android")
    alias(libs.plugins.ksp)
}

val parsedMapboxApiKey: String? by lazy {
    val property = findProperty("mapbox_api_key") as String?
    if (property == null) {
        logger.warn("No mapbox_api_key property set")
        null
    } else {
        property
    }
}

tasks.matching { it.name.startsWith("publish") }.configureEach {
    doFirst {
        requireNotNull(parsedMapboxApiKey) {
            "No Mapbox API key set"
        }
    }
}

android {
    commonConfig()

    buildFeatures.buildConfig = true

    defaultConfig {
        val mapboxApiKey = parsedMapboxApiKey ?: "mapbox_api_key_not_set"
        buildConfigField("String", "MAPBOX_API_KEY", "\"$mapboxApiKey\"")
    }
    namespace = "se.gustavkarlsson.skylight.android.lib.geocoder"
}

dependencies {
    implementation(project(":core"))
    implementation(project(":lib:location"))

    implementation(libs.mapbox.services)

    ksp(libs.kotlin.inject.compiler)
}
