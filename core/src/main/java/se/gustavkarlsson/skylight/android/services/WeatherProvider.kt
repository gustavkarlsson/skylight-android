package se.gustavkarlsson.skylight.android.services

import io.reactivex.Flowable
import io.reactivex.Single
import se.gustavkarlsson.koptional.Optional
import se.gustavkarlsson.skylight.android.entities.Location
import se.gustavkarlsson.skylight.android.entities.Report
import se.gustavkarlsson.skylight.android.entities.Weather

interface WeatherProvider {
	fun get(location: Single<Optional<Location>>): Single<Report<Weather>>
	fun stream(locations: Flowable<Optional<Location>>): Flowable<Report<Weather>>
}
