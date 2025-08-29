plugins {
    id("com.android.library")
    kotlin("android")
    alias(libs.plugins.ksp)
    alias(libs.plugins.sqldelight)
}

android {
    commonConfig()
    namespace = "se.gustavkarlsson.skylight.android.lib.places"
}

sqldelight {
    databases {
        create("Database") {
            schemaOutputDirectory = file("src/main/sqldelight")
            packageName.set("se.gustavkarlsson.skylight.android.lib.places")
            verifyMigrations = true
        }
    }
}

dependencies {
    implementation(project(":core"))
    implementation(project(":lib:location"))
    implementation(project(":lib:time"))

    implementation(libs.bundles.sqldelight)
    implementation(libs.androidx.datastore.preferences)

    ksp(libs.kotlin.inject.compiler)
}
