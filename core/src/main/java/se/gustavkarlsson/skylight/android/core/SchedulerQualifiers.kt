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
