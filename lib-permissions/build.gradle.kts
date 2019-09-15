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
	}
}

dependencies {
	implementation(project(":core"))

	implementation("com.tbruyelle.rxpermissions2:rxpermissions:${versions.rxpermissions}@aar")
	implementation("com.jakewharton.rxrelay2:rxrelay:${versions.rxrelay}")
	implementation("androidx.appcompat:appcompat:${versions.androidAppcompat}")
}
