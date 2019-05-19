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
	api("com.github.gustavkarlsson:krate:${versions.krate}")
	api("org.jetbrains.kotlin:kotlin-stdlib-jdk7:${versions.kotlin}")
	api("com.jakewharton.threetenabp:threetenabp:${versions.threetenabp}")
	api("com.jakewharton.timber:timber:${versions.timber}")
	api("com.github.gustavkarlsson:koptional:${versions.koptional}")
	api("io.reactivex.rxjava2:rxjava:${versions.rxjava}")
	api("io.reactivex.rxjava2:rxkotlin:${versions.rxkotlin}")
	api("org.koin:koin-android:${versions.koin}")
	api("com.github.ioki-mobility:TextRef:${versions.textref}")
}
