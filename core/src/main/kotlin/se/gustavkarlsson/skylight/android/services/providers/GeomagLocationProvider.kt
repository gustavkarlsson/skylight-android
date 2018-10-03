package se.gustavkarlsson.skylight.android.services.providers

import io.reactivex.Single
import se.gustavkarlsson.koptional.Optional
import se.gustavkarlsson.skylight.android.entities.GeomagLocation
import se.gustavkarlsson.skylight.android.entities.Location
import se.gustavkarlsson.skylight.android.entities.Report

interface GeomagLocationProvider {
	fun get(location: Single<Optional<Location>>): Single<Report<GeomagLocation>>
}
