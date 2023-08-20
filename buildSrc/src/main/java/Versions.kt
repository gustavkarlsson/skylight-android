import org.gradle.api.JavaVersion

object Versions {
    // Config
    const val minSdk = 26
    const val compileSdk = 34
    const val targetSdk = 34
    val java = JavaVersion.VERSION_1_8
    const val kotlin = "1.9.0" // Must also update in buildSrc/build.gradle.kts
    const val androidGradle = "8.2.0-alpha16" // Must also update in buildSrc/build.gradle.kts
    const val jgit = "5.12.0.202106070339-r"

    const val playPublisher = "3.7.0"
    const val ktlintGradle = "11.5.1"
    const val ktlint = "0.50.0"

    // App
    // Google
    const val googleServices = "4.3.15"
    const val playServicesLocation = "21.0.1"
    const val playServicesGcm = "17.0.0"

    const val crashlyticsGradle = "2.9.8"
    const val crashlytics = "18.4.0"

    const val androidAppcompat = "1.6.1"
    const val androidActivity = "1.7.2"
    const val androidAnnotation = "1.6.0"
    const val androidCore = "1.10.1"
    const val androidLifecycle = "2.6.1"
    const val androidWorkManager = "2.8.1"

    const val assent = "3.0.2"
    const val kotlinxCoroutines = "1.7.3"

    const val okHttp = "4.10.0"
    const val retrofit = "2.9.0"
    const val retrofitKotlinSerialization = "1.0.0"
    const val kotlinxSerialization = "1.5.1"

    const val arrow = "1.2.0"
    const val dagger = "2.47"
    const val anvil = "2.4.7"
    const val kotlinDateTime = "0.4.0"
    const val solarPositioning = "0.0.9"
    const val sqldelight = "1.5.5"
    const val dataStore = "1.0.0"
    const val protobuf = "3.24.0"
    const val protobufGradle = "0.9.4"
    const val leakcanary = "2.12"
    const val logcat = "0.1"
    const val mapboxServices = "5.8.0"
    const val conveyor = "cc93e41adc" // TODO use release version
    const val textref = "1.3.1"
    const val markwon = "4.6.2"
    const val store = "4.0.7" // TODO Upgrade to Store5
    const val composeBom = "2023.08.00"
    const val composeCompiler = "1.5.1"
    const val composeConstraintLayout = "1.0.1"
    const val accompanist = "0.31.6-rc"

    // Test
    const val junit = "4.13.2"
    const val assertk = "0.10"
    const val mockito = "2.22.0"
    const val mockitoKotlin = "1.6.0"
    const val commonsIo = "2.4"
}
