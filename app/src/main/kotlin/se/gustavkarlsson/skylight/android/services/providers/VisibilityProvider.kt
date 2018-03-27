package se.gustavkarlsson.skylight.android.services.providers

import io.reactivex.Single
import se.gustavkarlsson.skylight.android.entities.Location
import se.gustavkarlsson.skylight.android.entities.Visibility

interface VisibilityProvider {
	fun get(location: Single<Location>): Single<Visibility>
}
