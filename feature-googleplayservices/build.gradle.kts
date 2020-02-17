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
}

dependencies {
	implementation(project(":core"))
	implementation(project(":lib-ui"))

	implementation("com.google.android.gms:play-services-gcm:${Versions.playServicesGcm}")
	implementation("io.ashdavies.rx.rxtasks:rx-tasks:${Versions.rxtasks}")
}
