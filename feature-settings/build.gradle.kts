plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
    id("kotlin-android-extensions")
}

android {
    flavorDimensions("environment")

    productFlavors {
        create("production") {
            setDimension("environment")
            buildConfigField("boolean", "DEVELOP", "false")
        }

        create("develop") {
            setDimension("environment")
            buildConfigField("boolean", "DEVELOP", "true")
        }
    }
}

dependencies {
    implementation(project(":core"))
    implementation(project(":lib-ui"))

    kapt("com.google.dagger:dagger-compiler:${Versions.dagger}")
}
