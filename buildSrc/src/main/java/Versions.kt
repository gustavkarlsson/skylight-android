import org.gradle.api.JavaVersion

object Versions {
    // Config
    const val minSdk = 24
    const val compileSdk = 31
    const val targetSdk = 31
    val java = JavaVersion.VERSION_1_8
    const val kotlin = "1.6.21" // Must also update in buildSrc/build.gradle.kts
    const val androidGradle = "7.2.0" // Must also update in buildSrc/build.gradle.kts
    const val jgit = "5.12.0.202106070339-r"

    const val playPublisher = "3.7.0"
    const val ktlintGradle = "10.2.1"
    const val ktlint = "0.44.0" // Skip 0.45.0 as it seems borked

    // App
    // Google
    const val googleServices = "4.3.10"
    const val playServicesLocation = "19.0.1"
    const val playServicesGcm = "17.0.0"

    const val crashlyticsGradle = "2.8.1"
    const val crashlytics = "18.2.9"

    const val androidAppcompat = "1.4.1"
    const val androidActivity = "1.4.0"
    const val androidAnnotation = "1.3.0"
    const val androidCore = "1.7.0"
    const val androidLifecycle = "2.4.1"
    const val androidWorkManager = "2.7.1"

    const val assent = "3.0.0-RC4"
    const val kotlinxCoroutines = "1.6.0"

    const val okHttp = "4.9.3"
    const val retrofit = "2.9.0"
    const val retrofitKotlinSerialization = "0.8.0"
    const val kotlinxSerialization = "1.3.2"

    const val arrow = "1.0.1"
    const val dagger = "2.41"
    const val anvil = "2.3.11-1-6-10"
    const val threetenabp = "1.3.1"
    const val solarPositioning = "0.0.9"
    const val sqldelight = "1.5.3"
    const val dataStore = "1.0.0"
    const val protobuf = "3.19.4"
    const val protobufGradle = "0.8.18"
    const val leakcanary = "2.8.1"
    const val mapboxServices = "5.8.0"
    const val conveyor = "cc93e41adc" // TODO use release version
    const val textref = "1.3.1"
    const val markwon = "4.6.2"
    const val store = "4.0.5"
    const val compose = "1.2.0-beta01"
    const val composeConstraintLayout = "1.0.0"
    const val accompanist = "0.24.8-beta"

    // Test
    const val junit = "4.12"
    const val assertk = "0.10"
    const val mockito = "2.22.0"
    const val mockitoKotlin = "1.6.0"
    const val commonsIo = "2.4"
}
