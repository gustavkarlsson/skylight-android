import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.android.build.gradle.AppPlugin as AndroidAppPlugin
import com.android.build.gradle.BaseExtension as AndroidBaseExtension
import com.android.build.gradle.LibraryPlugin as AndroidLibraryPlugin

buildscript {
    repositories {
        jcenter()
        mavenCentral()
        google()
        maven { setUrl("https://maven.fabric.io/public") }
    }

    dependencies {
        classpath("com.android.tools.build:gradle:${Versions.androidGradle}")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}")
        classpath("com.google.gms:google-services:${Versions.googleServices}")
        classpath("io.fabric.tools:gradle:${Versions.fabric}")
        classpath("com.akaita.android:easylauncher:${Versions.easylauncher}")
        classpath("org.eclipse.jgit:org.eclipse.jgit:${Versions.jgit}")
        classpath("pl.allegro.tech.build:axion-release-plugin:${Versions.axionRelease}")
        classpath("com.github.triplet.gradle:play-publisher:${Versions.playPublisher}")
        classpath("com.squareup.sqldelight:gradle-plugin:${Versions.sqldelight}")
    }
}

allprojects {
    repositories {
        jcenter()
        mavenCentral()
        google()
        maven { setUrl("https://s3.amazonaws.com/repo.commonsware.com") }
        maven { setUrl("https://jitpack.io") }
        maven { setUrl("https://oss.sonatype.org/content/repositories/snapshots") }
        maven { setUrl("https://plugins.gradle.org/m2/") }
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions.jvmTarget = Versions.java.toString()
    }

    plugins.withType<AndroidLibraryPlugin> {
        extension.setupForAndroid()
    }

    plugins.withType<AndroidAppPlugin> {
        extension.setupForAndroid()
    }
}

fun AndroidBaseExtension.setupForAndroid() {
    compileSdkVersion(Versions.compileSdk)
    buildToolsVersion(Versions.buildTools)

    packagingOptions {
        exclude("META-INF/LICENSE")
        exclude("META-INF/NOTICE")
    }

    testOptions {
        unitTests.isReturnDefaultValues = true
        execution = "ANDROIDX_TEST_ORCHESTRATOR"
    }

    compileOptions {
        sourceCompatibility = Versions.java
        targetCompatibility = Versions.java
    }

    defaultConfig {
        minSdkVersion(Versions.minSdk)
        targetSdkVersion(Versions.targetSdk)
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunnerArgument("clearPackageData", "true")
    }
}
