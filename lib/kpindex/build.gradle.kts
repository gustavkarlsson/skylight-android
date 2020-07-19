plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
    kotlin("plugin.serialization") version Versions.kotlin
}

android {
    commonConfig()
}

dependencies {
    implementation(project(":core"))
    implementation(project(":lib:time"))

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:${Versions.retrofit}")
    implementation(
        "com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:" +
            Versions.retrofitKotlinSerialization
    )
    implementation("com.squareup.retrofit2:adapter-rxjava2:${Versions.retrofit}")

    kapt("com.google.dagger:dagger-compiler:${Versions.dagger}")

    // Testing
    testImplementation("junit:junit:${Versions.junit}")
    testImplementation("org.robolectric:robolectric:${Versions.robolectric}")
    testImplementation("org.mockito:mockito-inline:${Versions.mockito}")
    testImplementation("com.nhaarman:mockito-kotlin-kt1.1:${Versions.mockitoKotlin}") {
        exclude("org.jetbrains.kotlin")
    }
    testImplementation("commons-io:commons-io:${Versions.commonsIo}")
    testImplementation("com.willowtreeapps.assertk:assertk:${Versions.assertk}") {
        exclude("org.jetbrains.kotlin")
    }
}
