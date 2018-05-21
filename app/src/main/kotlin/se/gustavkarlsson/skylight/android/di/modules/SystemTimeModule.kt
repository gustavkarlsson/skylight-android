package se.gustavkarlsson.skylight.android.di.modules

import io.reactivex.Single
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import se.gustavkarlsson.skylight.android.services.providers.TimeProvider
import se.gustavkarlsson.skylight.android.services_impl.providers.SystemTimeWithFixedZoneIdProvider

class SystemTimeModule : TimeModule {

	override val timeProvider: TimeProvider by lazy {
		SystemTimeWithFixedZoneIdProvider(ZoneId.systemDefault())
	}

	override val now: Single<Instant> by lazy { timeProvider.getTime() }
}
