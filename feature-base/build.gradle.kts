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
	api(project(":core"))

	// Android
	api("androidx.fragment:fragment:${versions.androidFragment}")

	// Navigation
	api("android.arch.navigation:navigation-ui-ktx:${versions.navigation}")
	api("android.arch.navigation:navigation-fragment:${versions.navigation}")
	api("android.arch.navigation:navigation-ui:${versions.navigation}")

	// AutoDispose
	api("com.uber.autodispose:autodispose-kotlin:${versions.autodispose}")
	api("com.uber.autodispose:autodispose-android-kotlin:${versions.autodispose}")
	api("com.uber.autodispose:autodispose-android-archcomponents-kotlin:${versions.autodispose}")
}
