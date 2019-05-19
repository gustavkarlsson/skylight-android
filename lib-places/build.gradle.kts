plugins {
	id("com.android.library")
	id("kotlin-android")
	id("com.squareup.sqldelight")
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
	}
}

dependencies {
	implementation(project(":core"))

	implementation("com.squareup.sqldelight:android-driver:${versions.sqldelight}")
	implementation("com.squareup.sqldelight:rxjava2-extensions:${versions.sqldelight}")
}
