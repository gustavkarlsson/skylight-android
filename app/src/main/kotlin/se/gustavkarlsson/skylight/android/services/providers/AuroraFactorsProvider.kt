package se.gustavkarlsson.skylight.android.services.providers

import io.reactivex.Single
import org.threeten.bp.Instant
import se.gustavkarlsson.skylight.android.entities.AuroraFactors
import se.gustavkarlsson.skylight.android.entities.Location

interface AuroraFactorsProvider {
	fun get(time: Single<Instant>, location: Single<Location>): Single<AuroraFactors>
}
