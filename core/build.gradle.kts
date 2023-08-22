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
    api(platform(libs.compose.bom))
    api(platform(libs.firebase.bom))
    api(libs.androidx.core.ktx)
    api(libs.kotlinx.datetime)
    api(libs.arrow.core)
    api(libs.dagger)
    api(libs.textref)
    api(libs.kotlinx.coroutines.core)
    api(libs.androidx.appcompat)
}
