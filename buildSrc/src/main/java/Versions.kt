import org.gradle.api.JavaVersion

object Versions {
    // Config
    val minSdk = 23
    val compileSdk = 30
    val targetSdk = 30
    val buildTools = "30.0.2"
    val java = JavaVersion.VERSION_1_8
    val kotlin = "1.4.31" // Must also update in buildSrc/build.gradle.kts
    val androidGradle = "7.0.0-alpha12" // Must also update in buildSrc/build.gradle.kts
    val jgit = "5.6.1.202002131546-r"
    // Skip 1.11.0 as it has breaking bug: https://github.com/allegro/axion-release-plugin/issues/332
    // Skip 1.12.0 as it has breaking bug: https://github.com/allegro/axion-release-plugin/issues/339
    val axionRelease = "1.10.3"
    val playPublisher = "2.8.0"
    val ktlint = "9.4.1"

    // Dependencies
    val googleServices = "4.3.3"
    val playServicesLocation = "17.0.0"
    val playServicesGcm = "17.0.0"

    val firebaseAnalytics = "17.4.3"
    val crashlyticsGradle = "2.2.0"
    val crashlytics = "17.1.0"

    val androidAppcompat = "1.3.0-beta01"
    val androidFragment = "1.3.1"
    val androidAnnotation = "1.1.0"
    val androidPreference = "1.1.1"
    val androidWorkManager = "2.3.4"
    val androidCoreKtx = "1.3.0"
    val androidLifecycle = "2.2.0"

    val material = "1.1.0"

    val runtimePermissionKotlin = "1.1.2"
    val kotlinxCoroutines = "1.4.2"

    val okHttp = "4.9.1"
    val retrofit = "2.7.1"
    val retrofitKotlinSerialization = "0.7.0"

    val dagger = "2.28.1"
    val threetenabp = "1.2.4"
    val timber = "4.7.1"
    val solarPositioning = "0.0.9"
    val sqldelight = "1.4.4"
    val leakcanary = "2.4"
    val mapboxServices = "5.3.0"
    val conveyor = "ae2111d8df" // TODO use release version
    val koptional = "0.4.0"
    val simpleStack = "2.3.2"
    val textref = "1.3.1"
    val markwon = "4.4.0"
    val store = "4.0.0-beta01"
    val compose = "1.0.0-beta03"
    val composeConstraintLayout = "1.0.0-alpha04"
    val accompanist = "0.6.1"
    val composeThemeAdapter = "1.0.0-beta01"

    // Test
    val junit = "4.12"
    val assertk = "0.10"
    val mockito = "2.22.0"
    val mockitoKotlin = "1.6.0"
    val commonsIo = "2.4"
    val robolectric = "3.4.2"
}
