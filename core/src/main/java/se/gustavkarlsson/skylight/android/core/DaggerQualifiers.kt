package se.gustavkarlsson.skylight.android.core

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class Main

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class Io

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class Computation

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class Global

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class VersionCode

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class VersionName
