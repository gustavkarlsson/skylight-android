pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        google()
        maven { setUrl("https://s3.amazonaws.com/repo.commonsware.com") }
        maven { setUrl("https://jitpack.io") }
        maven { setUrl("https://oss.sonatype.org/content/repositories/snapshots") }
        maven { setUrl("https://plugins.gradle.org/m2/") }
        maven { setUrl("https://api.mapbox.com/downloads/v2/releases/maven") }
    }
}

include(
    ":app",
    ":core",

    ":lib:aurora",
    ":lib:darkness",
    ":lib:geocoder",
    ":lib:geomaglocation",
    ":lib:kpindex",
    ":lib:location",
    ":lib:navigation",
    ":lib:okhttp",
    ":lib:permissions",
    ":lib:places",
    ":lib:reversegeocoder",
    ":lib:runversion",
    ":lib:scopedservice",
    ":lib:settings",
    ":lib:time",
    ":lib:ui",
    ":lib:ui-compose",
    ":lib:weather",

    ":feature:about",
    ":feature:background",
    ":feature:googleplayservices",
    ":feature:intro",
    ":feature:main",
    ":feature:settings",
    ":feature:privacypolicy",
)
