plugins {
    id("com.android.library")
    kotlin("android")
    id("kotlin-android-extensions")
}

android {
    commonConfig()
    sourceSets["main"].res.srcDir("$buildDir/generated/privacypolicy")
}

dependencies {
    implementation(project(":core"))
    implementation(project(":lib:ui-legacy"))

    implementation("io.noties.markwon:core:${Versions.markwon}")
}

val copyPrivacyPolicyFile = task<Copy>("copyPrivacyPolicyFile") {
    description = "Copies the privacy policy file to a project res directory"
    from("$rootDir/PRIVACY_POLICY.md")
    into("$buildDir/generated/privacypolicy/raw")
    rename(String::toLowerCase)
}

tasks["preBuild"].dependsOn(copyPrivacyPolicyFile)
