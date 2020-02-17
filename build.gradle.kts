import org.gradle.kotlin.dsl.repositories
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

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
}
