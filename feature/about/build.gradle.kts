import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.RepositoryBuilder

buildscript {
    dependencies {
        classpath(libs.jgit)
    }
}

plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
    alias(libs.plugins.anvil)
    kotlin("plugin.parcelize")
}

android {
    commonConfig()
    composeConfig()

    buildFeatures.buildConfig = true

    defaultConfig {
        val repo = RepositoryBuilder().run {
            gitDir = File(rootDir, "/.git")
            readEnvironment()
            build()
        }
        val head = repo.exactRef("HEAD")
        val branch = Git(repo).branchList().call()
            .find { it.objectId == head.objectId }
            ?.name?.replace(".*/".toRegex(), "")
            ?: "None"
        val sha1 = head.objectId.name
        val buildTime = System.currentTimeMillis()

        buildConfigField("String", "GIT_BRANCH", "\"$branch\"")
        buildConfigField("String", "GIT_SHA1", "\"$sha1\"")
        buildConfigField("long", "BUILD_TIME_MILLIS", "${buildTime}L")
    }

    setFlavorDimensions(listOf("environment"))

    productFlavors {
        create("production") {
            dimension = "environment"
            buildConfigField("boolean", "DEVELOP", "false")
        }

        create("develop") {
            dimension = "environment"
            buildConfigField("boolean", "DEVELOP", "true")
        }
    }
    namespace = "se.gustavkarlsson.skylight.android.feature.about"
}

dependencies {
    implementation(project(":core"))
    implementation(project(":lib:ui-compose"))
    implementation(project(":lib:time"))

    kapt(libs.dagger.compiler)
}
