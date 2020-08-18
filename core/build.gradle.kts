plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
}

android {
    commonConfig()
}

dependencies {
    api("androidx.core:core-ktx:${Versions.androidCoreKtx}")
    api("com.jakewharton.threetenabp:threetenabp:${Versions.threetenabp}")
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
}
