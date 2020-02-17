plugins {
    id("com.android.library")
    id("kotlin-android")
}

dependencies {
    implementation(project(":core"))

    // Firebase
    implementation("com.google.firebase:firebase-core:${Versions.firebase}")
}
