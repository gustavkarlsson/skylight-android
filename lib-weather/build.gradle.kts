plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
}

android {
    defaultConfig {
        val openWeatherMapApiKey = findProperty("openweathermap_api_key") ?: run {
            logger.warn("WARNING: openweathermap_api_key not set")
            "openweathermap_api_key_not_set"
        }
        buildConfigField("String", "OPENWEATHERMAP_API_KEY", "\"$openWeatherMapApiKey\"")
    }
}

dependencies {
    implementation(project(":core"))

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:${Versions.retrofit}")
    implementation("com.squareup.retrofit2:converter-gson:${Versions.retrofit}")
    implementation("com.squareup.retrofit2:adapter-rxjava2:${Versions.retrofit}")

    // Dagger
    kapt("com.google.dagger:dagger-compiler:${Versions.dagger}")
}
