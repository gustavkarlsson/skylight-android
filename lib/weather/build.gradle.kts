plugins {
    id("com.android.library")
    kotlin("android")
    alias(libs.plugins.ksp)
    kotlin("plugin.serialization")
}

val parsedOpenWeatherMapApiKey: String? by lazy {
    val property = findProperty("openweathermap_api_key") as String?
    if (property == null) {
        logger.warn("No openweathermap_api_key property set")
        null
    } else {
        property
    }
}

tasks.matching { it.name.startsWith("publish") }.configureEach {
    doFirst {
        requireNotNull(parsedOpenWeatherMapApiKey) {
            "No OpenWeatherMap API key set"
        }
    }
}

android {
    commonConfig()

    buildFeatures.buildConfig = true

    defaultConfig {
        val openWeatherMapApiKey = parsedOpenWeatherMapApiKey ?: "openweathermap_api_key_not_set"
        buildConfigField("String", "OPENWEATHERMAP_API_KEY", "\"$openWeatherMapApiKey\"")
    }
    namespace = "se.gustavkarlsson.skylight.android.lib.weather"
}

dependencies {
    implementation(project(":core"))
    implementation(project(":lib:time"))
    implementation(project(":lib:location"))
    implementation(project(":lib:okhttp"))

    implementation(libs.bundles.retrofit)
    implementation(libs.bundles.store)

    ksp(libs.kotlin.inject.compiler)

    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.assertk) {
        exclude("org.jetbrains.kotlin")
    }
}
