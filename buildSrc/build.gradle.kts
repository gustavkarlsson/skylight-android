plugins {
    `kotlin-dsl`
}

repositories {
    jcenter()
    google()
}

dependencies {
    // Must also update in Versions.kt
    implementation("com.android.tools.build:gradle:7.0.0-alpha12")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.31")
}
