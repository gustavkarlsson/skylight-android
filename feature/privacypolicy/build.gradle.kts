plugins {
    id("com.android.library")
    kotlin("android")
    id("kotlin-parcelize")
}

android {
    commonConfig()
    composeConfig()
    sourceSets["main"].res.srcDir(layout.buildDirectory.dir("generated/privacypolicy"))
    namespace = "se.gustavkarlsson.skylight.android.feature.privacypolicy"
}

dependencies {
    implementation(project(":core"))
    implementation(project(":lib:ui-compose"))
}

val copyPrivacyPolicyFile = task<Copy>("copyPrivacyPolicyFile") {
    description = "Copies the privacy policy file to a project res directory"
    from("$rootDir/PRIVACY_POLICY.md")
    into(layout.buildDirectory.dir("generated/privacypolicy/raw"))
    rename(String::lowercase)
}

tasks["preBuild"].dependsOn(copyPrivacyPolicyFile)
