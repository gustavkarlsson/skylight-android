plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    google()
}

dependencies {
    // Must also update in Versions.kt
    implementation("com.android.tools.build:gradle:7.0.0-beta02")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.32")
}
