import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jlleitschuh.gradle.ktlint.KtlintExtension

plugins {
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.sqldelight) apply false
    alias(libs.plugins.ktlint) apply false
    alias(libs.plugins.anvil) apply false
    alias(libs.plugins.protobuf) apply false
    alias(libs.plugins.googleservices) apply false
    alias(libs.plugins.crashlytics) apply false
    alias(libs.plugins.playpublisher) apply false
}

allprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = freeCompilerArgs + "-opt-in=kotlin.RequiresOptIn"
        }
    }

    configure<KtlintExtension> {
        android.set(true)
        version.set("0.50.0")
        enableExperimentalRules.set(true)
    }

    afterEvaluate {
        extensions.findByType<KotlinProjectExtension>()?.apply {
            jvmToolchain(17)
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
