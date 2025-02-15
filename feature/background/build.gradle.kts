plugins {
    id("com.android.library")
    kotlin("android")
    alias(libs.plugins.ksp)
}

android {
    commonConfig()
    namespace = "se.gustavkarlsson.skylight.android.feature.background"
}

dependencies {
    implementation(project(":core"))
    implementation(project(":lib:ui")) // TODO only used for coloring notification?
    implementation(project(":lib:time"))
    implementation(project(":lib:settings"))
    implementation(project(":lib:location"))
    implementation(project(":lib:aurora"))
    implementation(project(":lib:places"))

    // WorkManager
    implementation(libs.androidx.workmanager.runtime.ktx)

    // DataStore
    implementation(libs.androidx.datastore.preferences)

    ksp(libs.kotlin.inject.compiler)

    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.assertk) {
        exclude("org.jetbrains.kotlin")
    }
}
