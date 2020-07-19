import com.android.build.gradle.AppExtension
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LibraryExtension

fun BaseExtension.commonConfig() {
    compileSdkVersion(Versions.compileSdk)
    buildToolsVersion(Versions.buildTools)

    packagingOptions {
        exclude("META-INF/LICENSE")
        exclude("META-INF/NOTICE")
    }

    testOptions {
        unitTests.isReturnDefaultValues = true
        execution = "ANDROIDX_TEST_ORCHESTRATOR"
    }

    compileOptions {
        sourceCompatibility = Versions.java
        targetCompatibility = Versions.java
    }

    defaultConfig {
        minSdkVersion(Versions.minSdk)
        targetSdkVersion(Versions.targetSdk)
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunnerArgument("clearPackageData", "true")
    }

    buildTypes {
        getByName("release") {
            when (this@commonConfig) {
                is AppExtension -> {
                    proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
                }
                is LibraryExtension -> {
                    consumerProguardFiles("proguard-rules.pro")
                }
            }
        }
    }
}
