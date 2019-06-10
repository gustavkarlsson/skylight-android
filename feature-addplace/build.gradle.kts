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

	defaultConfig {
		minSdkVersion(versions.minSdk)
		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
	}
}

dependencies {
	implementation(project(":core"))
	implementation(project(":lib-geocoder"))
	implementation(project(":lib-places"))
	implementation(project(":lib-ui"))
	implementation("com.github.gustavkarlsson:krate:${versions.krate}")
	implementation("com.github.ybq:Android-SpinKit:${versions.spinKit}")
}
