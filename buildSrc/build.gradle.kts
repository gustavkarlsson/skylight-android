plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    google()
}

dependencies {
    // Must also update in Versions.kt
    implementation("com.android.tools.build:gradle:8.2.0-alpha16")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.0")
}
