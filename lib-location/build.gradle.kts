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

	implementation("com.google.android.gms:play-services-location:${Versions.playServicesLocation}")
	implementation("com.patloew.rxlocation:rxlocation:${Versions.rxLocation}")
}
