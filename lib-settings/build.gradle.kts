plugins {
	id("com.android.library")
	id("kotlin-android")
	id("com.squareup.sqldelight")
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

	implementation("com.squareup.sqldelight:android-driver:${Versions.sqldelight}")
	implementation("com.squareup.sqldelight:rxjava2-extensions:${Versions.sqldelight}")
}
