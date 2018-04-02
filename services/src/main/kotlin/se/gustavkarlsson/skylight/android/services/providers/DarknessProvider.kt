package se.gustavkarlsson.skylight.android.services.providers

import io.reactivex.Single
import org.threeten.bp.Instant

import se.gustavkarlsson.skylight.android.entities.Darkness
import se.gustavkarlsson.skylight.android.entities.Location

interface DarknessProvider {
	fun get(time: Single<Instant>, location: Single<Location>): Single<Darkness>
}
