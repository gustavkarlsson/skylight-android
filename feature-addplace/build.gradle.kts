plugins {
	id("com.android.library")
	id("kotlin-android")
	id("kotlin-android-extensions")
}

android {
	compileSdkVersion(versions.compileSdk)

	compileOptions {
		sourceCompatibility = versions.java
		targetCompatibility = versions.java
	}

	sourceSets {
		getByName("main").java.setSrcDirs(listOf("src/main/kotlin"))
		getByName("test").java.setSrcDirs(listOf("src/test/kotlin"))
		getByName("androidTest").java.setSrcDirs(listOf("src/androidTest/kotlin"))
	}

	defaultConfig {
		minSdkVersion(versions.minSdk)
		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

		val mapboxApiKey = findProperty("mapbox_api_key") ?: run {
			logger.error("WARNING: mapbox_api_key not set")
			"mapbox_api_key_not_set"
		}
		buildConfigField("String", "MAPBOX_API_KEY", "\"$mapboxApiKey\"")
	}
}

dependencies {
	implementation(project(":core"))
	implementation(project(":feature-base"))

	implementation("com.mapbox.mapboxsdk:mapbox-sdk-services:${versions.mapboxService}")
}
