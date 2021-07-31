import com.google.protobuf.gradle.builtins
import com.google.protobuf.gradle.generateProtoTasks
import com.google.protobuf.gradle.protobuf
import com.google.protobuf.gradle.protoc

plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
    id("com.squareup.sqldelight")
    id("com.google.protobuf") version "0.8.17" // FIXME Versions.x
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
    implementation(project(":lib:places")) // FIXME remove dependency?

    // Legacy. Only exists for migration purposes
    implementation("com.squareup.sqldelight:android-driver:${Versions.sqldelight}")
    implementation("com.squareup.sqldelight:coroutines-extensions:${Versions.sqldelight}")

    implementation("androidx.datastore:datastore:${Versions.dataStore}")
    implementation("com.google.protobuf:protobuf-javalite:${Versions.protobuf}")

    kapt("com.google.dagger:dagger-compiler:${Versions.dagger}")
}
