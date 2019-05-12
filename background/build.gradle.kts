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
	implementation(project(":core"))
	implementation(project(":lib-analytics"))

	// Firebase
	implementation("com.google.firebase:firebase-core:${versions.firebase}")

	// Android Job
	implementation("com.evernote:android-job:${versions.androidjob}")

	// Ktx
	implementation("androidx.core:core-ktx:${versions.ktxCore}")

	// Testing
	testImplementation("org.robolectric:robolectric:${versions.robolectric}")
	testImplementation("junit:junit:${versions.junit}")
	testImplementation("org.mockito:mockito-inline:${versions.mockito}")
	testImplementation("com.nhaarman:mockito-kotlin-kt1.1:${versions.mockitoKotlin}") {
		exclude("org.jetbrains.kotlin")
	}
	testImplementation("com.willowtreeapps.assertk:assertk:${versions.assertk}") {
		exclude("org.jetbrains.kotlin")
	}
}
