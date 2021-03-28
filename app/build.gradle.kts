import pl.allegro.tech.build.axion.release.domain.TagNameSerializationConfig

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("kotlin-parcelize")
    id("com.google.firebase.crashlytics")
    id("pl.allegro.tech.build.axion-release")
    // id("com.github.triplet.play") version Versions.playPublisher // FIXME Re-enable when play plugin works
}

scmVersion {
    tag = TagNameSerializationConfig().apply {
        prefix = ""
    }
}

/*
FIXME Re-enable when play plugin works
play {
    serviceAccountCredentials = file("play-service-account.json")
    track = "alpha"
    defaultToAppBundles = true
}
*/

android {
    commonConfig()

    defaultConfig {
        applicationId = "se.gustavkarlsson.skylight.android"
        versionCode = generateVersionCode(scmVersion.version)
        versionName = scmVersion.version
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
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs["release"]
        }

        getByName("debug")
    }

    flavorDimensions("environment")

    productFlavors {
        create("production") {
            dimension = "environment"
            buildConfigField("boolean", "DEVELOP", "false")
        }

        create("develop") {
            dimension = "environment"
            buildConfigField("boolean", "DEVELOP", "true")
            versionNameSuffix = "-develop"
            applicationIdSuffix = ".develop"
        }
    }

    applicationVariants.all {
        val isProductionRelease = buildType.name == "release" && flavorName == "production"
        val manifestAName = buildString {
            if (isProductionRelease) {
                append(APP_NAME)
            } else {
                append(APP_INITIALS)
                append(' ')
                append(flavorName.take(3).capitalize())
                append(buildType.name.take(3).capitalize())
            }
        }
        resValue("string", "app_name_manifest", manifestAName)
    }
}

dependencies {
    implementation(project(":core"))
    implementation(project(":lib:analytics"))
    implementation(project(":lib:location"))
    implementation(project(":lib:okhttp"))
    implementation(project(":lib:weather"))
    implementation(project(":lib:darkness"))
    implementation(project(":lib:kpindex"))
    implementation(project(":lib:geomaglocation"))
    implementation(project(":lib:aurora"))
    implementation(project(":lib:geocoder"))
    implementation(project(":lib:navigationsetup"))
    implementation(project(":lib:scopedservice"))
    implementation(project(":lib:reversegeocoder"))
    implementation(project(":lib:runversion"))
    implementation(project(":lib:permissions"))
    implementation(project(":lib:places"))
    implementation(project(":lib:time"))
    implementation(project(":lib:ui"))
    implementation(project(":lib:ui-compose"))
    implementation(project(":lib:settings"))
    implementation(project(":feature:googleplayservices"))
    implementation(project(":feature:about"))
    implementation(project(":feature:intro"))
    implementation(project(":feature:background"))
    implementation(project(":feature:main"))
    implementation(project(":feature:privacypolicy"))

    implementation("androidx.annotation:annotation:${Versions.androidAnnotation}")

    // Dagger
    kapt("com.google.dagger:dagger-compiler:${Versions.dagger}")

    // Crashlytics
    implementation("com.google.firebase:firebase-crashlytics-ktx:${Versions.crashlytics}")

    // Leakcanary
    debugImplementation("com.squareup.leakcanary:leakcanary-android:${Versions.leakcanary}")

    // Timber
    implementation("com.jakewharton.timber:timber:${Versions.timber}")

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
