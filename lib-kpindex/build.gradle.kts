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

	// Retrofit
	implementation("com.squareup.retrofit2:retrofit:${versions.retrofit}")
	implementation("com.squareup.retrofit2:converter-gson:${versions.retrofit}")
	implementation("com.squareup.retrofit2:adapter-rxjava2:${versions.retrofit}")

	// Testing
	testImplementation("junit:junit:${versions.junit}")
	testImplementation("org.robolectric:robolectric:${versions.robolectric}")
	testImplementation("org.mockito:mockito-inline:${versions.mockito}")
	testImplementation("com.nhaarman:mockito-kotlin-kt1.1:${versions.mockitoKotlin}") {
		exclude("org.jetbrains.kotlin")
	}
	testImplementation("commons-io:commons-io:${versions.commonsIo}")
	testImplementation("com.willowtreeapps.assertk:assertk:${versions.assertk}") {
		exclude("org.jetbrains.kotlin")
	}
}
