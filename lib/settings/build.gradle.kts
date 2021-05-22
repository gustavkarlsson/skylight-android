plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
    id("com.squareup.sqldelight")
}

android {
    commonConfig()
}

sqldelight {
    database("Database") {
        schemaOutputDirectory = file("src/main/sqldelight")
        verifyMigrations = true
    }
}

dependencies {
    implementation(project(":core"))
    implementation(project(":lib:places"))

    implementation("com.squareup.sqldelight:android-driver:${Versions.sqldelight}")
    implementation("com.squareup.sqldelight:coroutines-extensions:${Versions.sqldelight}")

    kapt("com.google.dagger:dagger-compiler:${Versions.dagger}")
}
