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

	sourceSets {
		getByName("main").java.setSrcDirs(listOf("src/main/kotlin"))
		getByName("test").java.setSrcDirs(listOf("src/test/kotlin"))
		getByName("androidTest").java.setSrcDirs(listOf("src/androidTest/kotlin"))
	}

	defaultConfig {
		minSdkVersion(versions.minSdk)
		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
	}
}

dependencies {
	implementation(project(":core"))
}
