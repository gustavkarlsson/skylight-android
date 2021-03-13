plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
}

android {
    commonConfig()
    composeConfig()

    flavorDimensions("environment")

    productFlavors {
        create("production") {
            dimension("environment")
            buildConfigField("boolean", "DEVELOP", "false")
        }

        create("develop") {
            dimension("environment")
            buildConfigField("boolean", "DEVELOP", "true")
        }
    }
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

    implementation("com.github.gustavkarlsson.conveyor:conveyor-core:${Versions.conveyor}")

    kapt("com.google.dagger:dagger-compiler:${Versions.dagger}")
}
