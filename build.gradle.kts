import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jlleitschuh.gradle.ktlint.KtlintExtension

plugins {
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.sqldelight) apply false
    alias(libs.plugins.ktlint) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.protobuf) apply false
    alias(libs.plugins.googleservices) apply false
    alias(libs.plugins.crashlytics) apply false
    alias(libs.plugins.playpublisher) apply false
    alias(libs.plugins.compose.compiler) apply false
}

allprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")

    tasks.withType<KotlinCompile> {
        compilerOptions {
            freeCompilerArgs.add("-opt-in=kotlin.RequiresOptIn")
            freeCompilerArgs.add("-opt-in=kotlin.time.ExperimentalTime")
            freeCompilerArgs.add("-XXLanguage:+PropertyParamAnnotationDefaultTargetMode")
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

tasks.register<Delete>("clean") {
    delete(layout.buildDirectory)
}
