plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
    alias(libs.plugins.anvil)
    alias(libs.plugins.sqldelight)
    alias(libs.plugins.protobuf)
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
        artifact = "com.google.protobuf:protoc:${libs.versions.protobuf.get()}"
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
    implementation(libs.bundles.sqldelight)

    implementation(libs.androidx.datastore)
    implementation(libs.protobuf.javalite)

    kapt(libs.dagger.compiler)
}
