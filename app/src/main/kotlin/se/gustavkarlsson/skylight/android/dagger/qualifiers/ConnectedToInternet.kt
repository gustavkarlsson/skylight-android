package se.gustavkarlsson.skylight.android.dagger.qualifiers

import javax.inject.Qualifier
import kotlin.annotation.AnnotationRetention.RUNTIME

@Qualifier
@Retention(RUNTIME)
annotation class ConnectedToInternet
