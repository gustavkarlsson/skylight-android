import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions

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

	kotlinOptions {
		this as KotlinJvmOptions
		jvmTarget = Versions.java.toString()
	}

	defaultConfig {
		minSdkVersion(Versions.minSdk)
		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
	}
}

dependencies {
	api("org.jetbrains.kotlin:kotlin-stdlib-jdk7:${Versions.kotlin}")
	api("androidx.core:core-ktx:${Versions.coreKtx}")
	api("com.jakewharton.threetenabp:threetenabp:${Versions.threetenabp}")
	api("com.jakewharton.timber:timber:${Versions.timber}")
	api("com.jakewharton.rxrelay2:rxrelay:${Versions.rxrelay}")
	api("com.jakewharton.rx2:replaying-share-kotlin:${Versions.rxReplayingShare}")
	api("com.github.gustavkarlsson:koptional:${Versions.koptional}")
	api("io.reactivex.rxjava2:rxjava:${Versions.rxjava}")
	api("io.reactivex.rxjava2:rxkotlin:${Versions.rxkotlin}")
	api("org.koin:koin-android:${Versions.koin}")
	api("com.github.ioki-mobility:TextRef:${Versions.textref}")
}
