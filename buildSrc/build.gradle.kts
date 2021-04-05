plugins {
    `kotlin-dsl`
}

repositories {
    jcenter() // FIXME remove jcenter
    google()
}

dependencies {
    // Must also update in Versions.kt
    implementation("com.android.tools.build:gradle:7.0.0-alpha13")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.32")
}
