plugins {
    id("com.android.library")
    id("kotlin-android")
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
}
