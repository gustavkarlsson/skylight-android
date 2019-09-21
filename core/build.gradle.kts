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
	api("org.jetbrains.kotlin:kotlin-stdlib-jdk7:${versions.kotlin}")
	api("androidx.core:core-ktx:${versions.coreKtx}")
	api("com.jakewharton.threetenabp:threetenabp:${versions.threetenabp}")
	api("com.jakewharton.timber:timber:${versions.timber}")
	api("com.jakewharton.rxrelay2:rxrelay:${versions.rxrelay}")
	api("com.jakewharton.rx2:replaying-share-kotlin:${versions.rxReplayingShare}")
	api("com.github.gustavkarlsson:koptional:${versions.koptional}")
	api("io.reactivex.rxjava2:rxjava:${versions.rxjava}")
	api("io.reactivex.rxjava2:rxkotlin:${versions.rxkotlin}")
	api("org.koin:koin-android:${versions.koin}")
	api("com.github.ioki-mobility:TextRef:${versions.textref}")
}
