plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
    alias(libs.plugins.anvil)
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

    kapt(libs.dagger.compiler)

    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.assertk) {
        exclude("org.jetbrains.kotlin")
    }
}
