import org.gradle.api.JavaVersion

object Versions {
    // Config
    val minSdk = 23
    val compileSdk = 30
    val targetSdk = 30
    val java = JavaVersion.VERSION_1_8
    val kotlin = "1.5.10" // Must also update in buildSrc/build.gradle.kts
    val androidGradle = "7.0.0-beta03" // Must also update in buildSrc/build.gradle.kts
    val jgit = "5.12.0.202106070339-r"

    // Skip 1.11.0 as it has breaking bug: https://github.com/allegro/axion-release-plugin/issues/332
    // Skip 1.12.0 as it has breaking bug: https://github.com/allegro/axion-release-plugin/issues/339
    val axionRelease = "1.10.3"
    val playPublisher = "3.4.0-agp7.0"
    val ktlint = "10.1.0"

    // App
    // Google
    val googleServices = "4.3.8"
    val playServicesLocation = "18.0.0"
    val playServicesGcm = "17.0.0"

    val firebaseAnalytics = "19.0.0"
    val crashlyticsGradle = "2.7.0"
    val crashlytics = "18.0.1"

    val androidAppcompat = "1.3.0"
    val androidActivity = "1.3.0-beta01"
    val androidAnnotation = "1.2.0"
    val androidWorkManager = "2.5.0" // FIXME update to 2.6.0-beta01
    val androidCore = "1.5.0"
    val androidLifecycle = "2.3.1"

    val runtimePermissionKotlin = "1.1.2"
    val kotlinxCoroutines = "1.5.0"

    val okHttp = "4.9.1"
    val retrofit = "2.9.0"
    val retrofitKotlinSerialization = "0.8.0"
    val kotlinxSerialization = "1.2.1"

    val dagger = "2.37"
    val threetenabp = "1.3.1"
    val timber = "4.7.1"
    val solarPositioning = "0.0.9"
    val goldFinch = "1.1.1"
    val sqldelight = "1.5.0"
    val leakcanary = "2.7"
    val mapboxServices = "5.8.0"
    val conveyor = "cc93e41adc" // TODO use release version
    val koptional = "0.4.0"
    val textref = "1.3.1"
    val markwon = "4.6.2"
    val store = "4.0.2-KT15"
    val compose = "1.0.0-beta08"
    val composeConstraintLayout = "1.0.0-alpha07"
    val accompanist = "0.11.1"
    val backstack = "0.9.0+1.0.0-beta07"

    // Test
    val junit = "4.12"
    val assertk = "0.10"
    val mockito = "2.22.0"
    val mockitoKotlin = "1.6.0"
    val commonsIo = "2.4"
    val robolectric = "3.4.2"
}
