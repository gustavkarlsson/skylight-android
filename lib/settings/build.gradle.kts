plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
    id("com.squareup.anvil")
    id("com.squareup.sqldelight")
    id("com.google.protobuf")
}

android {
    commonConfig()
    namespace = "se.gustavkarlsson.skylight.android.lib.settings"
}

sqldelight {
    database("Database") {
        schemaOutputDirectory = file("src/main/sqldelight")
        verifyMigrations = true
    }
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:${Versions.protobuf}"
    }

    generateProtoTasks {
        all().forEach { task ->
            task.builtins {
                create("java") {
                    option("lite")
                }
            }
        }
    }
}

dependencies {
    implementation(project(":core"))
    implementation(project(":lib:places"))

    // Legacy. Only exists for migration purposes
    implementation("com.squareup.sqldelight:android-driver:${Versions.sqldelight}")
    implementation("com.squareup.sqldelight:coroutines-extensions:${Versions.sqldelight}")

    implementation("androidx.datastore:datastore:${Versions.dataStore}")
    implementation("com.google.protobuf:protobuf-javalite:${Versions.protobuf}")

    kapt("com.google.dagger:dagger-compiler:${Versions.dagger}")
}
