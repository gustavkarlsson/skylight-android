plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
    id("com.squareup.anvil")
    kotlin("plugin.serialization") version Versions.kotlin
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

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:${Versions.retrofit}")
    implementation(
        "com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:" +
            Versions.retrofitKotlinSerialization,
    )
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.kotlinxSerialization}")
    implementation("org.mobilenativefoundation.store:store4:${Versions.store}")

    kapt("com.google.dagger:dagger-compiler:${Versions.dagger}")

    // Testing
    testImplementation("junit:junit:${Versions.junit}")
    testImplementation("org.mockito:mockito-inline:${Versions.mockito}")
    testImplementation("com.nhaarman:mockito-kotlin-kt1.1:${Versions.mockitoKotlin}") {
        exclude("org.jetbrains.kotlin")
    }
    testImplementation("com.willowtreeapps.assertk:assertk:${Versions.assertk}") {
        exclude("org.jetbrains.kotlin")
    }
}
