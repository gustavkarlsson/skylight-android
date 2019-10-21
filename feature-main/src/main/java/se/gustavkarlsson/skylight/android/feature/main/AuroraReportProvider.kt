package se.gustavkarlsson.skylight.android.feature.main

import io.reactivex.Flowable
import io.reactivex.Single
import se.gustavkarlsson.skylight.android.entities.CompleteAuroraReport
import se.gustavkarlsson.skylight.android.entities.Loadable
import se.gustavkarlsson.skylight.android.entities.LoadableAuroraReport
import se.gustavkarlsson.skylight.android.entities.LocationResult

internal interface AuroraReportProvider {
	fun get(location: Single<LocationResult>): Single<CompleteAuroraReport>
	fun stream(locations: Flowable<Loadable<LocationResult>>): Flowable<LoadableAuroraReport>
}
