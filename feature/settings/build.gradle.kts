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
    implementation(project(":lib:analytics"))
    implementation(project(":lib:settings"))
    implementation(project(":lib:places"))

    implementation("androidx.preference:preference-ktx:${Versions.androidPreference}")

    kapt("com.google.dagger:dagger-compiler:${Versions.dagger}")
}
