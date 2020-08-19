plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
    id("kotlin-android-extensions")
}

android {
    commonConfig()

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
    implementation(project(":lib:ui"))
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

    implementation("androidx.cardview:cardview:${Versions.androidCardview}")

    // TODO replace with google material banner when available
    implementation("com.sergivonavi:materialbanner:${Versions.banner}")
    implementation("de.halfbit:knot:${Versions.knot}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-rx2:${Versions.kotlinxCoroutines}")

    kapt("com.google.dagger:dagger-compiler:${Versions.dagger}")
}
