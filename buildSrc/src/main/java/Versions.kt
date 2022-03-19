import org.gradle.api.JavaVersion

object Versions {
    // Config
    val minSdk = 24
    val compileSdk = 31
    val targetSdk = 31
    val java = JavaVersion.VERSION_1_8
    val kotlin = "1.6.10" // Must also update in buildSrc/build.gradle.kts
    val androidGradle = "7.1.1" // Must also update in buildSrc/build.gradle.kts
    val jgit = "5.12.0.202106070339-r"

    val playPublisher = "3.7.0"
    val ktlintGradle = "10.2.0"
    val ktlint = "0.43.2"

    // App
    // Google
    val googleServices = "4.3.10"
    val playServicesLocation = "18.0.0"
    val playServicesGcm = "17.0.0"

    val firebaseAnalytics = "20.0.0"
    val crashlyticsGradle = "2.8.0"
    val crashlytics = "18.2.4"

    val androidAppcompat = "1.3.1"
    val androidActivity = "1.4.0"
    val androidAnnotation = "1.3.0"
    val androidWorkManager = "2.7.0"
    val androidCore = "1.7.0"
    val androidLifecycle = "2.4.0"

    val runtimePermissionKotlin = "1.1.2"
    val kotlinxCoroutines = "1.6.0"

    val okHttp = "4.9.3"
    val retrofit = "2.9.0"
    val retrofitKotlinSerialization = "0.8.0"
    val kotlinxSerialization = "1.3.2"

    val arrow = "1.0.1"
    val dagger = "2.40.1"
    val anvil = "2.3.10-1-6-0"
    val threetenabp = "1.3.1"
    val solarPositioning = "0.0.9"
    val sqldelight = "1.5.2"
    val dataStore = "1.0.0"
    val protobuf = "3.19.1"
    val protobufGradle = "0.8.17"
    val leakcanary = "2.7"
    val mapboxServices = "5.8.0"
    val conveyor = "cc93e41adc" // TODO use release version
    val textref = "1.3.1"
    val markwon = "4.6.2"
    val store = "4.0.4-KT15"
    val compose = "1.1.0-rc01"
    val composeCompiler = "1.1.0-rc02" // Temporary diverge because Kotlin 1.6.10
    val composeConstraintLayout = "1.0.0-rc02"
    val accompanist = "0.22.0-rc"

    // Test
    val junit = "4.12"
    val assertk = "0.10"
    val mockito = "2.22.0"
    val mockitoKotlin = "1.6.0"
    val commonsIo = "2.4"
    val robolectric = "3.4.2"
}
