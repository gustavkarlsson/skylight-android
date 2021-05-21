import org.gradle.api.JavaVersion

object Versions {
    // Config
    val minSdk = 23
    val compileSdk = 30
    val targetSdk = 30
    val java = JavaVersion.VERSION_1_8
    val kotlin = "1.4.32" // Must also update in buildSrc/build.gradle.kts
    val androidGradle = "7.0.0-beta02" // Must also update in buildSrc/build.gradle.kts
    val jgit = "5.6.1.202002131546-r"

    // Skip 1.11.0 as it has breaking bug: https://github.com/allegro/axion-release-plugin/issues/332
    // Skip 1.12.0 as it has breaking bug: https://github.com/allegro/axion-release-plugin/issues/339
    val axionRelease = "1.10.3"
    val playPublisher = "2.8.0"
    val ktlint = "10.0.0"

    // Dependencies
    val googleServices = "4.3.5"
    val playServicesLocation = "18.0.0"
    val playServicesGcm = "17.0.0"

    val firebaseAnalytics = "18.0.2"
    val crashlyticsGradle = "2.5.1"
    val crashlytics = "17.3.1"

    val androidAppcompat = "1.3.0-rc01"
    val androidActivity = "1.3.0-alpha05"
    val androidAnnotation = "1.2.0"
    val androidWorkManager = "2.5.0"
    val androidCore = "1.3.2"
    val androidLifecycle = "2.3.1"

    val runtimePermissionKotlin = "1.1.2"
    val kotlinxCoroutines = "1.4.3"

    val okHttp = "4.9.1"
    val retrofit = "2.9.0"
    val retrofitKotlinSerialization = "0.7.0"

    val dagger = "2.33"
    val threetenabp = "1.3.0"
    val timber = "4.7.1"
    val solarPositioning = "0.0.9"
    val sqldelight = "1.4.4"
    val leakcanary = "2.7"
    val mapboxServices = "5.8.0"
    val conveyor = "ae2111d8df" // TODO use release version
    val koptional = "0.4.0"
    val textref = "1.3.1"
    val markwon = "4.4.0"
    val store = "4.0.0"
    val compose = "1.0.0-beta07"
    val composeConstraintLayout = "1.0.0-alpha07"
    val accompanist = "0.10.0"
    val backstack = "0.9.0+1.0.0-beta07"

    // Test
    val junit = "4.12"
    val assertk = "0.10"
    val mockito = "2.22.0"
    val mockitoKotlin = "1.6.0"
    val commonsIo = "2.4"
    val robolectric = "3.4.2"
}
