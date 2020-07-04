import org.gradle.api.JavaVersion

object Versions {
    // Config
    val minSdk = 21
    val compileSdk = 29
    val targetSdk = 29
    val buildTools = "29.0.3"
    val java = JavaVersion.VERSION_1_8
    val kotlin = "1.3.72"
    val androidGradle = "4.0.0" // Must also update in buildSrc/build.gradle.kts
    val easylauncher = "1.3.1"
    val jgit = "5.6.1.202002131546-r"
    val axionRelease = "1.12.0"
    val playPublisher = "2.8.0"
    val ktlint = "9.2.1"

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

    val rxjava = "2.2.18"
    val rxkotlin = "2.4.0"
    val rxandroid = "2.1.1"
    val rxbinding = "2.2.0"
    val rxrelay = "2.1.1"
    val rxReplayingShare = "2.2.0"
    val rxpermissions = "0.10.2"
    val rxLocation = "1.0.5"

    val okHttp = "3.14.4"
    val retrofit = "2.7.1"
    val retrofitKotlinSerialization = "0.5.0"

    val dagger = "2.26"
    val threetenabp = "1.2.2"
    val timber = "4.7.1"
    val solarPositioning = "0.0.9"
    val sqldelight = "1.2.2"
    val leakcanary = "2.2"
    val banner = "1.2.0"
    val mapboxServices = "5.0.0"
    val knot = "3.0.1" // 3.0.2 is broken (https://github.com/beworker/knot/issues/28)
    val edgeToEdge = "0.8"
    val koptional = "0.4.0"
    val simpleStack = "2.3.0"
    val rxtasks = "2.2.0"
    val textref = "1.2.0"

    // Test
    val junit = "4.12"
    val assertk = "0.10"
    val mockito = "2.22.0"
    val mockitoKotlin = "1.6.0"
    val commonsIo = "2.4"
    val robolectric = "3.4.2"
}
