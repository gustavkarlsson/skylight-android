plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
    id("com.squareup.anvil")
    id("com.squareup.sqldelight")
}

android {
    commonConfig()
    namespace = "se.gustavkarlsson.skylight.android.lib.places"
}

sqldelight {
    database("Database") {
        schemaOutputDirectory = file("src/main/sqldelight")
        verifyMigrations = true
    }
}

dependencies {
    implementation(project(":core"))
    implementation(project(":lib:location"))
    implementation(project(":lib:time"))

    implementation("com.squareup.sqldelight:android-driver:${Versions.sqldelight}")
    implementation("com.squareup.sqldelight:coroutines-extensions:${Versions.sqldelight}")
    implementation("androidx.datastore:datastore-preferences:${Versions.dataStore}")

    kapt("com.google.dagger:dagger-compiler:${Versions.dagger}")
}
