package se.gustavkarlsson.skylight.android.di.modules

import io.reactivex.Flowable
import io.reactivex.Single
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.services.Streamable
import se.gustavkarlsson.skylight.android.services.providers.AuroraReportProvider

interface AuroraReportModule {
	val auroraReportProvider: AuroraReportProvider
	val auroraReportSingle: Single<AuroraReport>
	val auroraReportStreamable: Streamable<AuroraReport>
	val auroraReportFlowable: Flowable<AuroraReport>
}
