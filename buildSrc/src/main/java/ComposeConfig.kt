import com.android.build.api.dsl.CommonExtension

fun CommonExtension<*, *, *, *, *, *, *, *>.composeConfig() {
    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerVersion = Versions.kotlin
        kotlinCompilerExtensionVersion = Versions.compose
    }
}
