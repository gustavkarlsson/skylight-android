package se.gustavkarlsson.skylight.android.services

import io.reactivex.Flowable
import io.reactivex.Single
import se.gustavkarlsson.koptional.Optional
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.entities.Location

interface AuroraReportProvider {
	fun get(location: Location?): Single<AuroraReport>
	fun stream(location: Location?): Flowable<AuroraReport>
}
