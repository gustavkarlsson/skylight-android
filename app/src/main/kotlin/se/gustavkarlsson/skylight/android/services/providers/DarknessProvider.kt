package se.gustavkarlsson.skylight.android.services.providers

import io.reactivex.Single
import org.threeten.bp.Instant

import se.gustavkarlsson.skylight.android.entities.Darkness
import se.gustavkarlsson.skylight.android.services.Location

interface DarknessProvider {
	fun getDarkness(time: Single<Instant>, location: Single<Location>): Single<Darkness>
}
