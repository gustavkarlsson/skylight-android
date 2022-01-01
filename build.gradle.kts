import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jlleitschuh.gradle.ktlint.KtlintExtension

buildscript {
    repositories {
        mavenCentral()
        google()
        maven("https://plugins.gradle.org/m2/")
    }

    dependencies {
        classpath("com.android.tools.build:gradle:${Versions.androidGradle}")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}")
        classpath("com.google.gms:google-services:${Versions.googleServices}")
        classpath("com.google.firebase:firebase-crashlytics-gradle:${Versions.crashlyticsGradle}")
        classpath("org.eclipse.jgit:org.eclipse.jgit:${Versions.jgit}")
        classpath("pl.allegro.tech.build:axion-release-plugin:${Versions.axionRelease}")
        classpath("com.squareup.sqldelight:gradle-plugin:${Versions.sqldelight}")
        classpath("org.jlleitschuh.gradle:ktlint-gradle:${Versions.ktlint}")
        classpath("com.google.protobuf:protobuf-gradle-plugin:${Versions.protobufGradle}")
    }
}

allprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")

    repositories {
        mavenCentral()
        google()
        maven { setUrl("https://s3.amazonaws.com/repo.commonsware.com") }
        maven { setUrl("https://jitpack.io") }
        maven { setUrl("https://oss.sonatype.org/content/repositories/snapshots") }
        maven { setUrl("https://plugins.gradle.org/m2/") }
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = Versions.java.toString()
            freeCompilerArgs = freeCompilerArgs + "-Xopt-in=kotlin.RequiresOptIn"
        }
    }

    configure<KtlintExtension> {
        android.set(true)
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
    delete(rootProject.buildDir)
}
