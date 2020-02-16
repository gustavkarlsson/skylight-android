import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions

plugins {
	id("com.android.library")
	id("kotlin-android")
	id("com.squareup.sqldelight")
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
	implementation(project(":core"))

	implementation("com.squareup.sqldelight:android-driver:${Versions.sqldelight}")
	implementation("com.squareup.sqldelight:rxjava2-extensions:${Versions.sqldelight}")
	implementation("de.halfbit:knot:${Versions.knot}")
}
