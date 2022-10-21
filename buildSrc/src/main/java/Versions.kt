import org.gradle.api.JavaVersion

object Versions {
    // Config
    const val minSdk = 24
    const val compileSdk = 33
    const val targetSdk = 33
    val java = JavaVersion.VERSION_1_8
    const val kotlin = "1.7.20" // Must also update in buildSrc/build.gradle.kts
    const val androidGradle = "7.3.1" // Must also update in buildSrc/build.gradle.kts
    const val jgit = "5.12.0.202106070339-r"

    const val playPublisher = "3.7.0"
    const val ktlintGradle = "11.0.0"
    const val ktlint = "0.44.0" // Skip 0.45.x as it has a bug: https://github.com/pinterest/ktlint/pull/1450

    // App
    // Google
    const val googleServices = "4.3.14"
    const val playServicesLocation = "21.0.0"
    const val playServicesGcm = "17.0.0"

    const val crashlyticsGradle = "2.9.2"
    const val crashlytics = "18.3.1"

    const val androidAppcompat = "1.5.1"
    const val androidActivity = "1.6.0"
    const val androidAnnotation = "1.5.0"
    const val androidCore = "1.9.0"
    const val androidLifecycle = "2.5.1"
    const val androidWorkManager = "2.7.1"

    const val assent = "3.0.0-RC4"
    const val kotlinxCoroutines = "1.6.4"

    const val okHttp = "4.10.0"
    const val retrofit = "2.9.0"
    const val retrofitKotlinSerialization = "0.8.0"
    const val kotlinxSerialization = "1.4.1"

    const val arrow = "1.1.3"
    const val dagger = "2.44"
    const val anvil = "2.4.2"
    const val threetenabp = "1.4.2" // TODO Migrate to kotlin time?
    const val solarPositioning = "0.0.9"
    const val sqldelight = "1.5.4"
    const val dataStore = "1.0.0"
    const val protobuf = "3.21.8"
    const val protobufGradle = "0.9.1"
    const val leakcanary = "2.9.1"
    const val logcat = "0.1"
    const val mapboxServices = "5.8.0"
    const val conveyor = "cc93e41adc" // TODO use release version
    const val textref = "1.3.1"
    const val markwon = "4.6.2"
    const val store = "4.0.5"
    const val compose = "1.3.0-rc01"
    const val composeCompiler = "1.3.2"
    const val composeConstraintLayout = "1.0.1"
    const val accompanist = "0.26.5-rc"

    // Test
    const val junit = "4.12"
    const val assertk = "0.10"
    const val mockito = "2.22.0"
    const val mockitoKotlin = "1.6.0"
    const val commonsIo = "2.4"
}
