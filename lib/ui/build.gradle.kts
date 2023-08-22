plugins {
    id("com.android.library")
    kotlin("android")
}

android {
    commonConfig()
    namespace = "se.gustavkarlsson.skylight.android.lib.ui"
}

dependencies {
    implementation(project(":core"))
    api(project(":lib:navigation"))
    api(project(":lib:scopedservice"))
}
