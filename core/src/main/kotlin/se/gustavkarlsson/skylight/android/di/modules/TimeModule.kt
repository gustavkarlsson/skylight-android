package se.gustavkarlsson.skylight.android.di.modules

import io.reactivex.Single
import org.threeten.bp.Instant
import se.gustavkarlsson.skylight.android.services.providers.TimeProvider

interface TimeModule {
    val timeProvider: TimeProvider
	val now: Single<Instant>
}
