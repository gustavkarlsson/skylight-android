plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
    kotlin("plugin.serialization") version Versions.kotlin
}

android {
    commonConfig()

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
    implementation(project(":lib:time"))
    implementation(project(":lib:location"))

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:${Versions.retrofit}")
    implementation(
        "com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:" +
            Versions.retrofitKotlinSerialization
    )
    implementation("com.dropbox.mobile.store:store4:${Versions.store}")

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
