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
	implementation(project(":lib-ui")) //TODO only used for coloring notification?

	// Firebase
	implementation("com.google.firebase:firebase-core:${Versions.firebase}")

	// Android Job
	implementation("com.evernote:android-job:${Versions.androidjob}")

	// Ktx
	implementation("androidx.core:core-ktx:${Versions.ktxCore}")

	// Testing
	testImplementation("org.robolectric:robolectric:${Versions.robolectric}")
	testImplementation("junit:junit:${Versions.junit}")
	testImplementation("org.mockito:mockito-inline:${Versions.mockito}")
	testImplementation("com.nhaarman:mockito-kotlin-kt1.1:${Versions.mockitoKotlin}") {
		exclude("org.jetbrains.kotlin")
	}
	testImplementation("com.willowtreeapps.assertk:assertk:${Versions.assertk}") {
		exclude("org.jetbrains.kotlin")
	}
}
