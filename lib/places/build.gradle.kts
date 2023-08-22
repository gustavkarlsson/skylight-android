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

    implementation(libs.bundles.sqldelight)
    implementation(libs.androidx.datastore.preferences)

    kapt(libs.dagger.compiler)
}
