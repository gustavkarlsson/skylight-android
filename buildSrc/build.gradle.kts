plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    google()
}

dependencies {
    implementation("com.android.tools.build:gradle:8.2.0-beta01")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.0") // Must also update in gradle/libs.versions.toml
}
