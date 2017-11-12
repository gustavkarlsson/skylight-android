package se.gustavkarlsson.skylight.android.services.providers

import io.reactivex.Single
import se.gustavkarlsson.skylight.android.entities.GeomagLocation
import se.gustavkarlsson.skylight.android.services.Location

interface GeomagLocationProvider {
	fun getGeomagLocation(location: Single<Location>): Single<GeomagLocation>
}
