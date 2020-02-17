import pl.allegro.tech.build.axion.release.domain.TagNameSerializationConfig

plugins {
	id("com.android.application")
	id("kotlin-android")
	id("kotlin-android-extensions")
	id("io.fabric")
	id("com.akaita.android.easylauncher")
	id("pl.allegro.tech.build.axion-release")
	id("com.github.triplet.play")
}

scmVersion {
	tag = TagNameSerializationConfig().apply {
		prefix = ""
	}
}

play {
	jsonFile = file("play-service-account.json")
	setTrack("alpha")
	uploadImages = true
}

android {
	defaultConfig {
		applicationId = "se.gustavkarlsson.skylight.android"
		versionCode = generateVersionCode(scmVersion.version)
		versionName = scmVersion.version
		multiDexEnabled = true
	}

	signingConfigs {
		create("release") {
			storeFile = File(findProperty("android_store_file") as String? ?: "dummy-keystore.jks")
			storePassword = findProperty("android_store_password") as String? ?: "password"
			keyAlias = findProperty("android_key_alias") as String? ?: "dummy"
			keyPassword = findProperty("android_key_password") as String? ?: "password"
		}
	}

	buildTypes {
		getByName("release") {
			isMinifyEnabled = false
			signingConfig = signingConfigs["release"]
		}

		getByName("debug")
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
			versionNameSuffix = "-develop"
			applicationIdSuffix = ".develop"
		}
	}

	applicationVariants.all {
		var appName = "Skylight"

		if (buildType.name != "release" || flavorName != "production") {
			appName += " ($flavorName${buildType.name.capitalize()})"
		}

		resValue("string", "app_name_variant", appName)
	}
}

dependencies {
	implementation(project(":core"))
	implementation(project(":lib-analytics"))
	implementation(project(":lib-location"))
	implementation(project(":lib-okhttp"))
	implementation(project(":lib-weather"))
	implementation(project(":lib-darkness"))
	implementation(project(":lib-kpindex"))
	implementation(project(":lib-geomaglocation"))
	implementation(project(":lib-geocoder"))
	implementation(project(":lib-navigation"))
	implementation(project(":lib-reversegeocoder"))
	implementation(project(":lib-permissions"))
	implementation(project(":lib-places"))
	implementation(project(":lib-ui"))
	implementation(project(":lib-settings"))
	implementation(project(":feature-googleplayservices"))
	implementation(project(":feature-about"))
	implementation(project(":feature-intro"))
	implementation(project(":feature-settings"))
	implementation(project(":feature-background"))
	implementation(project(":feature-addplace"))
	implementation(project(":feature-main"))

	implementation("androidx.multidex:multidex:${Versions.multidex}")
	implementation("androidx.appcompat:appcompat:${Versions.androidAppcompat}")
	implementation("androidx.annotation:annotation:${Versions.androidAnnotation}")

	// Lifecycle
	implementation("androidx.lifecycle:lifecycle-extensions:${Versions.archLifecycle}")

	// Crashlytics
	implementation("com.google.firebase:firebase-core:${Versions.firebase}")
	implementation("com.crashlytics.sdk.android:crashlytics:${Versions.crashlytics}")

	// Leakcanary
	debugImplementation("com.squareup.leakcanary:leakcanary-android:${Versions.leakcanary}")
	releaseImplementation("com.squareup.leakcanary:leakcanary-android-no-op:${Versions.leakcanary}")

	// Testing
	testImplementation("junit:junit:${Versions.junit}")
	testImplementation("org.mockito:mockito-inline:${Versions.mockito}")
	testImplementation("com.nhaarman:mockito-kotlin-kt1.1:${Versions.mockitoKotlin}") {
		exclude("org.jetbrains.kotlin")
	}
	testImplementation("com.willowtreeapps.assertk:assertk:${Versions.assertk}") {
		exclude("org.jetbrains.kotlin")
	}

	// Instrumentation testing
	androidTestImplementation("com.willowtreeapps.assertk:assertk:${Versions.assertk}") {
		exclude("org.jetbrains.kotlin")
	}
	androidTestImplementation("androidx.annotation:annotation:${Versions.androidAnnotation}")
	androidTestImplementation("androidx.test:runner:${Versions.androidSupportTest}")
	androidTestImplementation("androidx.test.ext:junit:${Versions.androidSupportTest}")
	androidTestImplementation("androidx.test:rules:${Versions.androidSupportTest}")
	androidTestUtil("androidx.test:orchestrator:${Versions.androidSupportTest}")
	androidTestImplementation("androidx.test.espresso:espresso-core:${Versions.espresso}")
	androidTestImplementation("androidx.test.espresso:espresso-intents:${Versions.espressoIntents}")
	androidTestImplementation("org.mockito:mockito-android:${Versions.mockito}")
	androidTestImplementation("com.nhaarman:mockito-kotlin-kt1.1:${Versions.mockitoKotlin}") {
		exclude("org.jetbrains.kotlin")
	}
	androidTestImplementation("commons-io:commons-io:${Versions.commonsIo}")
	androidTestImplementation("com.agoda.kakao:kakao:${Versions.kakao}")
	androidTestImplementation("androidx.test.uiautomator:uiautomator:${Versions.uiAutomator}")
}

/**
 * Calculates an Android version code from a version number string.<br>
 * <br>
 * Note that versions containing the string "snapshot" (case insensitive) will get a lower
 * version code than a release version, since release versions come after snapshot Versions.<br>
 * <br>
 * Examples:
 * <ul>
 *     <li>"0.0.1-SNAPSHOT" => 100</li><
 *     <li>"1.2.3-SNAPSHOT" => 1020300</li>
 *     <li>"11.22.33" => 11223301</li>
 * </ul>
 * @param version A version number following "semantic versioning"
 * @return A version code
 */
fun generateVersionCode(version: String): Int {
	val type = if (version.toLowerCase().contains("snapshot")) 0 else 1
	val (major, minor, patch) = version
		.split('.')
		.map { it.replace("\\D".toRegex(), "") }
		.map { it.toInt() }
	val versionCode = (major * 1000000) + (minor * 10000) + (patch * 100) + type
	require(major in 0..2000)
	require(minor in 0..99)
	require(patch in 0..99)
	require(type in 0..99)
	require(versionCode in 1..2100000000)
	return versionCode
}

// Recommended to be applied at bottom
apply(mapOf("plugin" to "com.google.gms.google-services"))
