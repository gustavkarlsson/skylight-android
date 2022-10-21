plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
    id("com.squareup.anvil")
    kotlin("plugin.serialization") version Versions.kotlin
}

android {
    commonConfig()
    namespace = "se.gustavkarlsson.skylight.android.lib.kpindex"
}

dependencies {
    implementation(project(":core"))
    implementation(project(":lib:time"))

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:${Versions.retrofit}")
    implementation(
        "com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:" +
            Versions.retrofitKotlinSerialization,
    )
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.kotlinxSerialization}")
    implementation("com.dropbox.mobile.store:store4:${Versions.store}")

    kapt("com.google.dagger:dagger-compiler:${Versions.dagger}")

    // Testing
    testImplementation("junit:junit:${Versions.junit}")
    testImplementation("org.mockito:mockito-inline:${Versions.mockito}")
    testImplementation("com.nhaarman:mockito-kotlin-kt1.1:${Versions.mockitoKotlin}") {
        exclude("org.jetbrains.kotlin")
    }
    testImplementation("commons-io:commons-io:${Versions.commonsIo}")
    testImplementation("com.willowtreeapps.assertk:assertk:${Versions.assertk}") {
        exclude("org.jetbrains.kotlin")
    }
}
