plugins {
    id("com.android.library")
    kotlin("android")
    alias(libs.plugins.ksp)
    kotlin("plugin.parcelize")
    alias(libs.plugins.compose.compiler)
}

android {
    commonConfig()
    composeConfig()

    buildFeatures.buildConfig = true

    setFlavorDimensions(listOf("environment"))

    productFlavors {
        create("production") {
            dimension = "environment"
            buildConfigField("boolean", "DEVELOP", "false")
        }

        create("develop") {
            dimension = "environment"
            buildConfigField("boolean", "DEVELOP", "true")
        }
    }
    namespace = "se.gustavkarlsson.skylight.android.feature.main"
}

dependencies {
    implementation(project(":core"))
    implementation(project(":lib:ui-compose"))
    implementation(project(":lib:time"))
    implementation(project(":lib:weather"))
    implementation(project(":lib:places"))
    implementation(project(":lib:location"))
    implementation(project(":lib:aurora"))
    implementation(project(":lib:permissions"))
    implementation(project(":lib:darkness"))
    implementation(project(":lib:kpindex"))
    implementation(project(":lib:geomaglocation"))
    implementation(project(":lib:reversegeocoder"))
    implementation(project(":lib:geocoder"))
    implementation(project(":lib:settings"))

    implementation(libs.conveyor.core)

    ksp(libs.kotlin.inject.compiler)
}
