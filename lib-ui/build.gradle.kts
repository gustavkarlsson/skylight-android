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
	implementation(project(":lib-analytics"))
	api(project(":lib-navigation"))

	api("com.google.android.material:material:${versions.androidMaterial}")
	api("androidx.fragment:fragment:${versions.androidFragment}")
	api("androidx.constraintlayout:constraintlayout:${versions.constraintLayout}")

	// AutoDispose
	api("com.uber.autodispose:autodispose-kotlin:${versions.autodispose}")
	api("com.uber.autodispose:autodispose-android-kotlin:${versions.autodispose}")
	api("com.uber.autodispose:autodispose-android-archcomponents-kotlin:${versions.autodispose}")

	// Koin
	api("org.koin:koin-androidx-viewmodel:${versions.koin}")
	api("org.koin:koin-androidx-scope:${versions.koin}")

	// Rx misc
	api("io.reactivex.rxjava2:rxandroid:${versions.rxandroid}")

	// RxBinding
	api("com.jakewharton.rxbinding2:rxbinding-kotlin:${versions.rxbinding}")
	api("com.jakewharton.rxbinding2:rxbinding-support-v4-kotlin:${versions.rxbinding}")
	api("com.jakewharton.rxbinding2:rxbinding-appcompat-v7-kotlin:${versions.rxbinding}")
	api("com.jakewharton.rxbinding2:rxbinding-design-kotlin:${versions.rxbinding}")
	api("com.jakewharton.rxbinding2:rxbinding-recyclerview-v7-kotlin:${versions.rxbinding}")
}
