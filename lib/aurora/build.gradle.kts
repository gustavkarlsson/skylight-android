plugins {
    id("com.android.library")
    kotlin("android")
    alias(libs.plugins.ksp)
}

android {
    commonConfig()
    namespace = "se.gustavkarlsson.skylight.android.lib.aurora"
}

dependencies {
    implementation(project(":core"))
    implementation(project(":lib:weather"))
    implementation(project(":lib:darkness"))
    implementation(project(":lib:kpindex"))
    implementation(project(":lib:geomaglocation"))
    implementation(project(":lib:reversegeocoder"))
    implementation(project(":lib:location"))

    ksp(libs.kotlin.inject.compiler)

    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.assertk) {
        exclude("org.jetbrains.kotlin")
    }
}
