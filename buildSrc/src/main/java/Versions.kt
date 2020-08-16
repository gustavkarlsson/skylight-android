import org.gradle.api.JavaVersion

object Versions {
    // Config
    val minSdk = 23
    val compileSdk = 29
    val targetSdk = 29
    val buildTools = "29.0.3"
    val java = JavaVersion.VERSION_1_8
    val kotlin = "1.3.72"
    val androidGradle = "4.0.0" // Must also update in buildSrc/build.gradle.kts
    val jgit = "5.6.1.202002131546-r"
    // Skip 1.11.0 as it has breaking bug: https://github.com/allegro/axion-release-plugin/issues/332
    // Skip 1.12.0 as it has breaking bug: https://github.com/allegro/axion-release-plugin/issues/339
    val axionRelease = "1.10.3"
    val playPublisher = "2.8.0"
    val ktlint = "9.3.0"

    // Dependencies
    val googleServices = "4.3.3"
    val playServicesLocation = "17.0.0"
    val playServicesGcm = "17.0.0"

    val firebaseAnalytics = "17.4.3"
    val crashlyticsGradle = "2.2.0"
    val crashlytics = "17.1.0"

    val androidAppcompat = "1.1.0"
    val androidFragment = "1.2.5"
    val androidAnnotation = "1.1.0"
    val androidCardview = "1.0.0"
    val androidPreference = "1.1.1"
    val androidWorkManager = "2.3.4"
    val androidConstraintLayout = "2.0.0-beta6"
    val androidCoreKtx = "1.3.0"
    val androidLifecycle = "2.2.0"

    val material = "1.1.0"

    val rxjava = "2.2.19"
    val rxkotlin = "2.4.0"
    val rxandroid = "2.1.1"
    val rxbinding = "3.1.0"
    val rxrelay = "2.1.1"
    val rxReplayingShare = "2.2.0"
    val rxpermissions = "0.10.2"
    val rxLocation = "1.0.5"
    val kotlinxCoroutines = "1.3.8"

    val okHttp = "3.14.4"
    val retrofit = "2.7.1"
    val retrofitKotlinSerialization = "0.5.0"

    val dagger = "2.28.1"
    val threetenabp = "1.2.4"
    val timber = "4.7.1"
    val solarPositioning = "0.0.9"
    val sqldelight = "1.2.2"
    val leakcanary = "2.4"
    val banner = "1.2.0"
    val mapboxServices = "5.3.0"
    val knot = "3.0.3"
    val edgeToEdge = "0.10"
    val koptional = "0.4.0"
    val simpleStack = "2.3.2"
    val textref = "1.3.0"
    val markwon = "4.4.0"
    val store = "4.0.0-alpha06"

    // Test
    val junit = "4.12"
    val assertk = "0.10"
    val mockito = "2.22.0"
    val mockitoKotlin = "1.6.0"
    val commonsIo = "2.4"
    val robolectric = "3.4.2"
}
