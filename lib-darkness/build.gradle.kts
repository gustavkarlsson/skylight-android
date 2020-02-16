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
	implementation(project(":core"))

	implementation("net.e175.klaus:solarpositioning:${Versions.solarPositioning}")
}
