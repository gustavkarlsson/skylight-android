plugins {
    id("com.android.library")
    kotlin("android")
    alias(libs.plugins.ksp)
    alias(libs.plugins.sqldelight)
    alias(libs.plugins.protobuf)
}

android {
    commonConfig()
    namespace = "se.gustavkarlsson.skylight.android.lib.settings"
}

sqldelight {
    databases {
        create("Database") {
            schemaOutputDirectory = file("src/main/sqldelight")
            packageName.set("se.gustavkarlsson.skylight.android.lib.settings")
            verifyMigrations = true
        }
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

    ksp(libs.kotlin.inject.compiler)
}
