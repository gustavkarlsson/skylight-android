plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
}

dependencies {
    implementation(project(":core"))
    implementation(project(":lib-ui")) // TODO only used for coloring notification?

    // WorkManager
    implementation("androidx.work:work-runtime:${Versions.workManager}")
    implementation("androidx.work:work-runtime-ktx:${Versions.workManager}")
    implementation("androidx.work:work-rxjava2:${Versions.workManager}")

    // Ktx
    implementation("androidx.core:core-ktx:${Versions.ktxCore}")

    // Dagger
    kapt("com.google.dagger:dagger-compiler:${Versions.dagger}")

    // Testing
    testImplementation("org.robolectric:robolectric:${Versions.robolectric}")
    testImplementation("junit:junit:${Versions.junit}")
    testImplementation("org.mockito:mockito-inline:${Versions.mockito}")
    testImplementation("com.nhaarman:mockito-kotlin-kt1.1:${Versions.mockitoKotlin}") {
        exclude("org.jetbrains.kotlin")
    }
    testImplementation("com.willowtreeapps.assertk:assertk:${Versions.assertk}") {
        exclude("org.jetbrains.kotlin")
    }
}
