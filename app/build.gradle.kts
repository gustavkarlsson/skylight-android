import pl.allegro.tech.build.axion.release.domain.TagNameSerializationConfig

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("kotlin-android-extensions")
    id("com.google.firebase.crashlytics")
    id("com.akaita.android.easylauncher")
    id("pl.allegro.tech.build.axion-release")
    id("com.github.triplet.play") version Versions.playPublisher
}

scmVersion {
    tag = TagNameSerializationConfig().apply {
        prefix = ""
    }
}

play {
    serviceAccountCredentials = file("play-service-account.json")
    track = "alpha"
}

android {
    commonConfig()

    defaultConfig {
        applicationId = "se.gustavkarlsson.skylight.android"
        versionCode = generateVersionCode(scmVersion.version)
        versionName = scmVersion.version
        multiDexEnabled = true
    }

    signingConfigs {
        create("release") {
            storeFile = findProperty("android_store_file")?.let { File(it as String) } ?: file("dummy-keystore.jks")
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
            buildConfigField("boolean", "DEVELOP", "false")
        }

        create("develop") {
            setDimension("environment")
            buildConfigField("boolean", "DEVELOP", "true")
            versionNameSuffix = "-develop"
            applicationIdSuffix = ".develop"
        }
    }

    applicationVariants.all {
        val isProductionRelease = buildType.name == "release" && flavorName == "production"
        val appName = buildString {
            if (isProductionRelease) {
                append("Skylight")
            } else {
                append("SL")
                append(' ')
                append(flavorName.take(3).capitalize())
                append(buildType.name.take(3).capitalize())
            }
        }
        resValue("string", "app_name", appName)
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
    implementation(project(":lib-aurora"))
    implementation(project(":lib-geocoder"))
    implementation(project(":lib-navigationsetup"))
    implementation(project(":lib-scopedservice"))
    implementation(project(":lib-reversegeocoder"))
    implementation(project(":lib-runversion"))
    implementation(project(":lib-permissions"))
    implementation(project(":lib-places"))
    implementation(project(":lib-time"))
    implementation(project(":lib-ui"))
    implementation(project(":lib-settings"))
    implementation(project(":feature-googleplayservices"))
    implementation(project(":feature-about"))
    implementation(project(":feature-intro"))
    implementation(project(":feature-settings"))
    implementation(project(":feature-background"))
    implementation(project(":feature-addplace"))
    implementation(project(":feature-main"))

    implementation("androidx.annotation:annotation:${Versions.androidAnnotation}")

    // Dagger
    kapt("com.google.dagger:dagger-compiler:${Versions.dagger}")

    // Lifecycle // FIXME why does it compile without these?
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:${Versions.androidLifecycle}")
    kapt("androidx.lifecycle:lifecycle-compiler:${Versions.androidLifecycle}")

    // Crashlytics
    implementation("com.google.firebase:firebase-crashlytics-ktx:${Versions.crashlytics}")

    // RxAndroid
    implementation("io.reactivex.rxjava2:rxandroid:${Versions.rxandroid}")

    // Leakcanary
    debugImplementation("com.squareup.leakcanary:leakcanary-android:${Versions.leakcanary}")

    // Testing
    testImplementation("junit:junit:${Versions.junit}")
    testImplementation("org.mockito:mockito-inline:${Versions.mockito}")
    testImplementation("com.nhaarman:mockito-kotlin-kt1.1:${Versions.mockitoKotlin}") {
        exclude("org.jetbrains.kotlin")
    }
    testImplementation("com.willowtreeapps.assertk:assertk:${Versions.assertk}") {
        exclude("org.jetbrains.kotlin")
    }
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

// Recommended to be applied at bottom (still?)
apply(mapOf("plugin" to "com.google.gms.google-services"))
