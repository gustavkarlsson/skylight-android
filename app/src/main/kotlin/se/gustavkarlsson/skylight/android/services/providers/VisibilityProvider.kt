package se.gustavkarlsson.skylight.android.services.providers

import io.reactivex.Single
import se.gustavkarlsson.skylight.android.entities.Visibility
import se.gustavkarlsson.skylight.android.services.Location

interface VisibilityProvider {
	fun getVisibility(location: Single<Location>): Single<Visibility>
}
