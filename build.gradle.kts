import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jlleitschuh.gradle.ktlint.KtlintExtension

plugins {
    id("org.jetbrains.kotlin.plugin.serialization") version libs.versions.kotlin apply false
    id("com.squareup.sqldelight") version libs.versions.sqldelight apply false
    id("org.jlleitschuh.gradle.ktlint") version libs.versions.ktlintGradle apply false
    id("com.squareup.anvil") version libs.versions.anvil apply false
    id("com.google.protobuf") version libs.versions.protobufGradle apply false
    id("com.google.gms.google-services") version libs.versions.googleServices apply false
    id("com.google.firebase.crashlytics") version libs.versions.crashlyticsGradle apply false
    id("com.github.triplet.play") version libs.versions.playPublisher apply false
}

allprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = Versions.java.toString()
            freeCompilerArgs = freeCompilerArgs + "-opt-in=kotlin.RequiresOptIn"
        }
    }

    configure<KtlintExtension> {
        android.set(true)
        version.set(Versions.ktlint)
        enableExperimentalRules.set(true)
    }

    afterEvaluate {
        extensions.findByType<KotlinProjectExtension>()?.apply {
            sourceSets.all {
                languageSettings {
                    progressiveMode = true
                }
            }
        }
    }
}

task<Delete>("clean") {
    delete(rootProject.layout.buildDirectory)
}
