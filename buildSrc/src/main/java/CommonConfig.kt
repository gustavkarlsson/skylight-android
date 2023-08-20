import com.android.build.api.dsl.CommonExtension

fun CommonExtension<*, *, *, *, *>.commonConfig() {
    compileSdk = Versions.compileSdk

    packaging {
        resources.excludes += "META-INF/LICENSE"
        resources.excludes += "META-INF/NOTICE"
        resources.excludes += "META-INF/*.kotlin_module"
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
        minSdk = Versions.minSdk
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunnerArguments["clearPackageData"] = "true"
    }
}
