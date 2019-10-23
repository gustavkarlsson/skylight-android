package se.gustavkarlsson.skylight.android.services

import io.reactivex.Flowable
import io.reactivex.Single
import se.gustavkarlsson.skylight.android.entities.Darkness
import se.gustavkarlsson.skylight.android.entities.Loadable
import se.gustavkarlsson.skylight.android.entities.LocationResult
import se.gustavkarlsson.skylight.android.entities.Report

interface DarknessProvider {
	fun get(location: Single<LocationResult>): Single<Report<Darkness>>
	fun stream(locations: Flowable<Loadable<LocationResult>>): Flowable<Loadable<Report<Darkness>>>
}
