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
	implementation(project(":lib-analytics"))
	api(project(":lib-navigation"))

	api("com.google.android.material:material:${Versions.androidMaterial}")
	api("androidx.fragment:fragment:${Versions.androidFragment}")
	api("androidx.constraintlayout:constraintlayout:${Versions.constraintLayout}")

	// AutoDispose
	api("com.uber.autodispose:autodispose-kotlin:${Versions.autodispose}")
	api("com.uber.autodispose:autodispose-android-kotlin:${Versions.autodispose}")
	api("com.uber.autodispose:autodispose-android-archcomponents-kotlin:${Versions.autodispose}")

	// Koin
	api("org.koin:koin-androidx-viewmodel:${Versions.koin}")
	api("org.koin:koin-androidx-scope:${Versions.koin}")

	// Rx misc
	api("io.reactivex.rxjava2:rxandroid:${Versions.rxandroid}")

	// RxBinding
	api("com.jakewharton.rxbinding2:rxbinding-kotlin:${Versions.rxbinding}")
	api("com.jakewharton.rxbinding2:rxbinding-support-v4-kotlin:${Versions.rxbinding}")
	api("com.jakewharton.rxbinding2:rxbinding-appcompat-v7-kotlin:${Versions.rxbinding}")
	api("com.jakewharton.rxbinding2:rxbinding-design-kotlin:${Versions.rxbinding}")
	api("com.jakewharton.rxbinding2:rxbinding-recyclerview-v7-kotlin:${Versions.rxbinding}")
}
