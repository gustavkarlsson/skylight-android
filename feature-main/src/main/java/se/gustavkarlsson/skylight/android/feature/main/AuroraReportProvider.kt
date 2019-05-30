package se.gustavkarlsson.skylight.android.feature.main

import io.reactivex.Flowable
import io.reactivex.Single
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.entities.Location

internal interface AuroraReportProvider {
	fun get(location: Location?): Single<AuroraReport>
	fun stream(location: Location?): Flowable<AuroraReport>
}
