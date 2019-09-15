plugins {
	id("com.android.library")
	id("kotlin-android")
	id("kotlin-android-extensions")
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

	flavorDimensions("environment")

	productFlavors {
		create("production") {
			setDimension("environment")
			buildConfigField("boolean", "DEVELOP", "false" )
		}

		create("develop") {
			setDimension("environment")
			buildConfigField("boolean", "DEVELOP", "true" )
		}
	}
}

dependencies {
	implementation(project(":core"))
	implementation(project(":lib-ui"))
	implementation(project(":lib-weather"))
	implementation(project(":lib-kpindex"))
	implementation(project(":lib-darkness"))
	implementation(project(":lib-settings"))
	implementation(project(":lib-geomaglocation"))
	implementation(project(":lib-reversegeocoder"))
	implementation(project(":lib-location"))
	implementation(project(":lib-permissions"))
	implementation(project(":lib-places"))

	implementation("androidx.cardview:cardview:${versions.androidCardview}")
	// TODO replace with google material banner when available
	implementation("com.sergivonavi:materialbanner:${versions.banner}")
	implementation("de.halfbit:knot:${versions.knot}")

}
