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
	}
}

dependencies {
	implementation(project(":core"))

	// Google Play Services
	implementation("com.google.android.gms:play-services-gcm:${Versions.playServicesGcm}")
	implementation("com.google.android.gms:play-services-location:${Versions.playServicesLocation}")

	// Reactive Location
	implementation("pl.charmas.android:android-reactive-location2:${Versions.reactiveLocation}@aar")
}
