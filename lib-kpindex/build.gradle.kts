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

	// Retrofit
	implementation("com.squareup.retrofit2:retrofit:${Versions.retrofit}")
	implementation("com.squareup.retrofit2:converter-gson:${Versions.retrofit}")
	implementation("com.squareup.retrofit2:adapter-rxjava2:${Versions.retrofit}")

	// Testing
	testImplementation("junit:junit:${Versions.junit}")
	testImplementation("org.robolectric:robolectric:${Versions.robolectric}")
	testImplementation("org.mockito:mockito-inline:${Versions.mockito}")
	testImplementation("com.nhaarman:mockito-kotlin-kt1.1:${Versions.mockitoKotlin}") {
		exclude("org.jetbrains.kotlin")
	}
	testImplementation("commons-io:commons-io:${Versions.commonsIo}")
	testImplementation("com.willowtreeapps.assertk:assertk:${Versions.assertk}") {
		exclude("org.jetbrains.kotlin")
	}
}
