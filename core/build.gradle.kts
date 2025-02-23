plugins {
    id("com.android.library")
    kotlin("android")
    alias(libs.plugins.ksp)
}

android {
    commonConfig()
    defaultConfig {
        resValue("string", "app_name", APP_NAME)
    }
    namespace = "se.gustavkarlsson.skylight.android.core"
}

dependencies {
    api(platform(libs.compose.bom))
    api(platform(libs.firebase.bom))
    api(libs.androidx.core.ktx)
    api(libs.kotlinx.datetime)
    api(libs.arrow.core)
    api(libs.kotlin.inject.runtime)
    api(libs.textref)
    api(libs.kotlinx.coroutines.core)
    api(libs.androidx.appcompat)
    api(libs.androidx.activity.compose)

    ksp(libs.kotlin.inject.compiler)
}
