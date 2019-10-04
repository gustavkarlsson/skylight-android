plugins {
	id("com.android.library")
	id("kotlin-android")
}

android {
	compileSdkVersion(Versions.compileSdk)

	compileOptions {
		sourceCompatibility = Versions.java
		targetCompatibility = Versions.java
	}

	defaultConfig {
		minSdkVersion(Versions.minSdk)
		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

		val openWeatherMapApiKey = findProperty("openweathermap_api_key") ?: run {
			logger.error("WARNING: openweathermap_api_key not set")
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
}
