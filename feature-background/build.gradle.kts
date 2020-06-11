plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
}

android {
    commonConfig()
}

dependencies {
    implementation(project(":core"))
    implementation(project(":lib-ui")) // TODO only used for coloring notification?
    implementation(project(":lib-analytics"))
    implementation(project(":lib-time"))
    implementation(project(":lib-settings"))
    implementation(project(":lib-location"))

    // WorkManager
    implementation("androidx.work:work-runtime-ktx:${Versions.androidWorkManager}")
    implementation("androidx.work:work-rxjava2:${Versions.androidWorkManager}")

    kapt("com.google.dagger:dagger-compiler:${Versions.dagger}")

    // Testing
    testImplementation("junit:junit:${Versions.junit}")
    testImplementation("org.mockito:mockito-inline:${Versions.mockito}")
    testImplementation("com.nhaarman:mockito-kotlin-kt1.1:${Versions.mockitoKotlin}") {
        exclude("org.jetbrains.kotlin")
    }
    testImplementation("com.willowtreeapps.assertk:assertk:${Versions.assertk}") {
        exclude("org.jetbrains.kotlin")
    }
}
