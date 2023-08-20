plugins {
    id("com.android.library")
    kotlin("android")
    id("com.squareup.anvil")
}

android {
    commonConfig()
    defaultConfig {
        resValue("string", "app_name", APP_NAME)
    }
    namespace = "se.gustavkarlsson.skylight.android.core"
}

dependencies {
    implementation(platform("androidx.compose:compose-bom:${Versions.composeBom}"))
    api("androidx.core:core-ktx:${Versions.androidCore}")
    api("org.jetbrains.kotlinx:kotlinx-datetime:${Versions.kotlinDateTime}")
    api("io.arrow-kt:arrow-core:${Versions.arrow}")
    api("com.google.dagger:dagger:${Versions.dagger}")
    api("com.github.ioki-mobility:TextRef:${Versions.textref}")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.kotlinxCoroutines}")
    api("androidx.appcompat:appcompat:${Versions.androidAppcompat}")
}
