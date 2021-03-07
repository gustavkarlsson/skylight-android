import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.RepositoryBuilder
import java.io.File

plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
}

android {
    commonConfig()
    composeConfig()

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

    flavorDimensions("environment")

    productFlavors {
        create("production") {
            dimension("environment")
            buildConfigField("boolean", "DEVELOP", "false")
        }

        create("develop") {
            dimension("environment")
            buildConfigField("boolean", "DEVELOP", "true")
        }
    }
}

dependencies {
    implementation(project(":core"))
    implementation(project(":lib:ui-compose"))
    implementation(project(":lib:time"))

    kapt("com.google.dagger:dagger-compiler:${Versions.dagger}")
}
