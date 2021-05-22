plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
}

android {
    commonConfig()
    defaultConfig {
        resValue("string", "app_name", APP_NAME)
    }
}

dependencies {
    api("androidx.core:core-ktx:${Versions.androidCore}")
    api("com.jakewharton.threetenabp:threetenabp:${Versions.threetenabp}")
    api("com.github.gustavkarlsson:koptional:${Versions.koptional}")
    api("com.google.dagger:dagger:${Versions.dagger}")
    api("com.github.ioki-mobility:TextRef:${Versions.textref}")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.kotlinxCoroutines}")
    api("androidx.appcompat:appcompat:${Versions.androidAppcompat}")

    kapt("com.google.dagger:dagger-compiler:${Versions.dagger}")
}
