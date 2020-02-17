plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-android-extensions")
}

dependencies {
    implementation(project(":core"))
    implementation(project(":lib-ui"))
    implementation("de.halfbit:knot:${Versions.knot}")
}
