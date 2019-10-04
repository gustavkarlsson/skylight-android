plugins {
	id("com.android.library")
	id("kotlin-android")
	id("kotlin-android-extensions")
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

	flavorDimensions("environment")

	productFlavors {
		create("production") {
			setDimension("environment")
			buildConfigField("boolean", "DEVELOP", "false" )
		}

		create("develop") {
			setDimension("environment")
			buildConfigField("boolean", "DEVELOP", "true" )
		}
	}
}

dependencies {
	implementation(project(":core"))
	implementation(project(":lib-ui"))

	implementation("androidx.preference:preference:${Versions.androidPreference}")
}
