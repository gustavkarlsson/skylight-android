import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.RepositoryBuilder
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
	compileSdkVersion(versions.compileSdk)
	buildToolsVersion(versions.buildTools)

	compileOptions {
		sourceCompatibility = versions.java
		targetCompatibility = versions.java
	}

	sourceSets {
		getByName("main").java.setSrcDirs(listOf("src/main/kotlin"))
		getByName("test").java.setSrcDirs(listOf("src/test/kotlin"))
		getByName("androidTest").java.setSrcDirs(listOf("src/androidTest/kotlin"))
	}

	packagingOptions {
		exclude("META-INF/LICENSE")
		exclude("META-INF/NOTICE")
	}

	testOptions {
		unitTests.isReturnDefaultValues = true
		execution = "ANDROIDX_TEST_ORCHESTRATOR"
	}

	defaultConfig {
		applicationId = "se.gustavkarlsson.skylight.android"
		minSdkVersion(versions.minSdk)
		targetSdkVersion(versions.targetSdk)
		versionCode = generateVersionCode(scmVersion.version)
		versionName = scmVersion.version
		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
		testInstrumentationRunnerArgument("clearPackageData", "true")
		multiDexEnabled = true

		val repo = RepositoryBuilder().run {
			gitDir = File(rootDir, "/.git")
			readEnvironment()
			build()
		}
		val head = repo.exactRef("HEAD")
		val branch = Git(repo).branchList().call()
			.find { it.objectId == head.objectId }
			?.name?.replace(".*/".toRegex(), "")
			?: "None"
		val sha1 = head.objectId.name
		val buildTime = System.currentTimeMillis()

		buildConfigField("String", "GIT_BRANCH", "\"$branch\"")
		buildConfigField("String", "GIT_SHA1", "\"$sha1\"")
		buildConfigField("long", "BUILD_TIME_MILLIS", "${buildTime}L")
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
		}

		create("develop") {
			setDimension("environment")
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
	implementation(project(":background"))
	implementation(project(":analytics"))
	implementation(project(":location"))
	implementation(project(":weather"))
	implementation(project(":darkness"))
	implementation(project(":kpindex"))
	implementation(project(":geomaglocation"))
	implementation(project(":locationname"))

	implementation("com.google.android.material:material:${versions.androidMaterial}")
	implementation("androidx.multidex:multidex:${versions.multidex}")
	implementation("androidx.appcompat:appcompat:${versions.androidAppcompat}")
	implementation("androidx.fragment:fragment:${versions.androidFragment}")
	implementation("androidx.annotation:annotation:${versions.androidAnnotation}")
	implementation("androidx.preference:preference:${versions.androidPreference}")
	implementation("androidx.cardview:cardview:${versions.androidCardview}")
	implementation("androidx.constraintlayout:constraintlayout:${versions.constraintLayout}")

	// Google Play Services
	implementation("com.google.android.gms:play-services-gcm:${versions.playServicesGcm}")

	// Navigation
	implementation("android.arch.navigation:navigation-fragment:${versions.navigation}")
	implementation("android.arch.navigation:navigation-ui:${versions.navigation}")
	implementation("android.arch.navigation:navigation-ui-ktx:${versions.navigation}")

	// Rx misc
	implementation("io.reactivex.rxjava2:rxandroid:${versions.rxandroid}")
	implementation("com.jakewharton.rxrelay2:rxrelay:${versions.rxrelay}")
	implementation("com.tbruyelle.rxpermissions2:rxpermissions:${versions.rxpermissions}@aar")
	implementation("com.f2prateek.rx.preferences2:rx-preferences:${versions.rxpreferences}")
	implementation("io.ashdavies.rx.rxtasks:rx-tasks:${versions.rxtasks}")

	// RxBinding
	implementation("com.jakewharton.rxbinding2:rxbinding-kotlin:${versions.rxbinding}")
	implementation("com.jakewharton.rxbinding2:rxbinding-support-v4-kotlin:${versions.rxbinding}")
	implementation("com.jakewharton.rxbinding2:rxbinding-appcompat-v7-kotlin:${versions.rxbinding}")
	implementation("com.jakewharton.rxbinding2:rxbinding-design-kotlin:${versions.rxbinding}")
	implementation("com.jakewharton.rxbinding2:rxbinding-recyclerview-v7-kotlin:${versions.rxbinding}")

	// AutoDispose
	implementation("com.uber.autodispose:autodispose-kotlin:${versions.autodispose}")
	implementation("com.uber.autodispose:autodispose-android-kotlin:${versions.autodispose}")
	implementation("com.uber.autodispose:autodispose-android-archcomponents-kotlin:${versions.autodispose}")

	// Lifecycle
	implementation("androidx.lifecycle:lifecycle-extensions:${versions.archLifecycle}")

	// Crashlytics
	implementation("com.google.firebase:firebase-core:${versions.firebase}")
	implementation("com.crashlytics.sdk.android:crashlytics:${versions.crashlytics}")

	// Leakcanary
	debugImplementation("com.squareup.leakcanary:leakcanary-android:${versions.leakcanary}")
	releaseImplementation("com.squareup.leakcanary:leakcanary-android-no-op:${versions.leakcanary}")

	// Koin
	implementation("org.koin:koin-androidx-viewmodel:${versions.koin}")
	implementation("org.koin:koin-androidx-scope:${versions.koin}")

	// Testing
	testImplementation("junit:junit:${versions.junit}")
	testImplementation("org.mockito:mockito-inline:${versions.mockito}")
	testImplementation("com.nhaarman:mockito-kotlin-kt1.1:${versions.mockitoKotlin}") {
		exclude("org.jetbrains.kotlin")
	}
	testImplementation("com.willowtreeapps.assertk:assertk:${versions.assertk}") {
		exclude("org.jetbrains.kotlin")
	}

	// Instrumentation testing
	androidTestImplementation("androidx.annotation:annotation:${versions.androidAnnotation}")
	androidTestImplementation("androidx.test:runner:${versions.androidSupportTest}")
	androidTestImplementation("androidx.test:rules:${versions.androidSupportTest}")
	androidTestUtil("androidx.test:orchestrator:${versions.androidSupportTest}")
	androidTestImplementation("androidx.test.espresso:espresso-core:${versions.espresso}")
	androidTestImplementation("org.mockito:mockito-android:${versions.mockito}")
	androidTestImplementation("com.nhaarman:mockito-kotlin-kt1.1:${versions.mockitoKotlin}") {
		exclude("org.jetbrains.kotlin")
	}
	androidTestImplementation("commons-io:commons-io:${versions.commonsIo}")
	androidTestImplementation("com.agoda.kakao:kakao:${versions.kakao}")
}

/**
 * Calculates an Android version code from a version number string.<br>
 * <br>
 * Note that versions containing the string "snapshot" (case insensitive) will get a lower
 * version code than a release version, since release versions come after snapshot versions.<br>
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
