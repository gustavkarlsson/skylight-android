plugins {
	id("com.android.library")
	id("kotlin-android")
}

android {
	compileSdkVersion(versions.compileSdk)

	compileOptions {
		sourceCompatibility = versions.java
		targetCompatibility = versions.java
	}

	defaultConfig {
		minSdkVersion(versions.minSdk)
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
	implementation("com.squareup.retrofit2:retrofit:${versions.retrofit}")
	implementation("com.squareup.retrofit2:converter-gson:${versions.retrofit}")
	implementation("com.squareup.retrofit2:adapter-rxjava2:${versions.retrofit}")
}
