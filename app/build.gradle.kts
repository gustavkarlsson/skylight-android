plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    alias(libs.plugins.anvil)
    kotlin("plugin.parcelize")
    alias(libs.plugins.googleservices)
    alias(libs.plugins.crashlytics)
    alias(libs.plugins.playpublisher)
}

val versionNameProperty: String? by lazy {
    val property = findProperty("version_name") as String?
    if (property == null) {
        logger.debug("No version_name property set")
        null
    } else {
        property
    }
}

val parsedVersionName: String? by lazy {
    val versionName = versionNameProperty?.trim()?.takeIf { it.matches(Regex("\\d{1,2}\\.\\d{1,2}\\.\\d{1,2}")) }
    if (versionName == null) {
        logger.debug("Could not parse version name from '$versionNameProperty'")
        null
    } else {
        versionName
    }
}

val parsedVersionCode: Int? by lazy {
    val versionName = parsedVersionName
    if (versionName == null) {
        logger.debug("Could not parse version code from '$versionName'")
        null
    } else {
        generateVersionCode(versionName)
    }
}

/**
 * Calculates an Android version code from a version number string.<br>
 * Because of legacy reasons, every code ends with 01
 * Examples:
 * <ul>
 *     <li>"0.0.1" => 101</li><
 *     <li>"1.2.3" => 1020301</li>
 *     <li>"11.22.33" => 11223301</li>
 * </ul>
 * @param version A version number following "semantic versioning"
 * @return A version code
 */
fun generateVersionCode(version: String): Int {
    val type = 1
    val (major, minor, patch) = version
        .split('.')
        .map { it.replace("\\D".toRegex(), "") }
        .map { it.toInt() }
    require(major in 0..2000)
    require(minor in 0..99)
    require(patch in 0..99)
    require(type in 0..99)
    val versionCode = (major * 1000000) + (minor * 10000) + (patch * 100) + type
    require(versionCode in 1..2100000000)
    return versionCode
}

tasks.matching { it.name.startsWith("publish") }.configureEach {
    doFirst {
        requireNotNull(parsedVersionName) {
            "No version name set"
        }
        requireNotNull(parsedVersionCode) {
            "No version code set"
        }
    }
}

play {
    serviceAccountCredentials.set(file("play-service-account.json"))
    track.set("alpha")
    defaultToAppBundles.set(true)
}

android {
    commonConfig()
    composeConfig()

    buildFeatures.buildConfig = true

    defaultConfig {
        applicationId = "se.gustavkarlsson.skylight.android"
        targetSdk = 34
        versionCode = parsedVersionCode ?: 99999999
        versionName = parsedVersionName ?: "99.99.99"
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
            proguardFile(getDefaultProguardFile("proguard-android-optimize.txt"))
            proguardFile("proguard-rules.pro")
        }

        getByName("debug")
    }

    setFlavorDimensions(listOf("environment"))

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

        @Suppress("DEPRECATION")
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
    packaging {
        with(resources.excludes) {
            add("META-INF/licenses/**")
            add("META-INF/AL2.0")
            add("META-INF/LGPL2.1")
        }
    }
    namespace = "se.gustavkarlsson.skylight.android"
}

dependencies {
    implementation(project(":core"))
    implementation(project(":lib:location"))
    implementation(project(":lib:okhttp"))
    implementation(project(":lib:weather"))
    implementation(project(":lib:darkness"))
    implementation(project(":lib:kpindex"))
    implementation(project(":lib:geomaglocation"))
    implementation(project(":lib:aurora"))
    implementation(project(":lib:geocoder"))
    implementation(project(":lib:navigation"))
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
    implementation(project(":feature:settings"))

    implementation(libs.androidx.annotation)

    // Dagger
    kapt(libs.dagger.compiler)

    // Crashlytics
    implementation(libs.firebase.crashlytics.ktx)

    // Leakcanary
    debugImplementation(libs.leakcanary.android)

    // Logcat
    implementation(libs.logcat)

    // Compose
    implementation(libs.androidx.activity.compose)
    implementation(libs.compose.animation)

    // Testing
    // TODO Make version catalog bundle
    testImplementation(libs.junit)
    testImplementation(libs.assertk) {
        exclude("org.jetbrains.kotlin")
    }
}
