import com.android.build.api.dsl.CommonExtension

fun CommonExtension<*, *, *, *>.commonConfig() {
    compileSdk = Versions.compileSdk

    packagingOptions {
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

    buildTypes {
        getByName("release") {
            proguardFile("proguard-rules.pro")
        }
    }

    lint {
        // TODO Re-enable once this bug has been fixed: https://github.com/JakeWharton/timber/issues/408
        disable(
            "LogNotTimber",
            "StringFormatInTimber",
            "ThrowableNotAtBeginning",
            "BinaryOperationInTimber",
            "TimberArgCount",
            "TimberArgTypes",
            "TimberTagLength",
            "TimberExceptionLogging",
        )
    }
}
