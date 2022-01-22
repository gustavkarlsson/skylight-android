plugins {
    id("com.android.library")
    id("kotlin-android")
}

android {
    commonConfig()
}

dependencies {
    implementation(project(":core"))
    api(project(":lib:navigation"))
    api(project(":lib:scopedservice"))
}
