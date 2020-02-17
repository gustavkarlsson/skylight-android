plugins {
    id("com.android.library")
    id("kotlin-android")
}

dependencies {
    implementation(project(":core"))

    implementation("com.squareup.okhttp3:okhttp:${Versions.okHttp}")
}
