plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
}

android {
    commonConfig()
}

dependencies {
    api("org.jetbrains.kotlin:kotlin-stdlib-jdk7:${Versions.kotlin}")
    api("androidx.core:core-ktx:${Versions.androidCoreKtx}")
    api("androidx.preference:preference-ktx:${Versions.androidPreferences}")
    api("com.jakewharton.threetenabp:threetenabp:${Versions.threetenabp}")
    api("com.jakewharton.timber:timber:${Versions.timber}")
    api("com.jakewharton.rxrelay2:rxrelay:${Versions.rxrelay}")
    api("com.jakewharton.rx2:replaying-share-kotlin:${Versions.rxReplayingShare}")
    api("com.github.gustavkarlsson:koptional:${Versions.koptional}")
    api("io.reactivex.rxjava2:rxjava:${Versions.rxjava}")
    api("io.reactivex.rxjava2:rxkotlin:${Versions.rxkotlin}")
    api("com.google.dagger:dagger:${Versions.dagger}")
    api("com.github.ioki-mobility:TextRef:${Versions.textref}")
    api("androidx.fragment:fragment-ktx:${Versions.androidFragment}")
    api("androidx.appcompat:appcompat:${Versions.androidAppcompat}")

    kapt("com.google.dagger:dagger-compiler:${Versions.dagger}")

    // Testing
    testImplementation("junit:junit:${Versions.junit}")
    testImplementation("org.mockito:mockito-inline:${Versions.mockito}")
    testImplementation("com.nhaarman:mockito-kotlin-kt1.1:${Versions.mockitoKotlin}") {
        exclude("org.jetbrains.kotlin")
    }
    testImplementation("com.willowtreeapps.assertk:assertk:${Versions.assertk}") {
        exclude("org.jetbrains.kotlin")
    }
}
