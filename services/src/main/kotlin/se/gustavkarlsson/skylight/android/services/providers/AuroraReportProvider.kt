package se.gustavkarlsson.skylight.android.services.providers

import io.reactivex.Single
import se.gustavkarlsson.skylight.android.entities.AuroraReport

interface AuroraReportProvider {
	fun get(): Single<AuroraReport>
}
