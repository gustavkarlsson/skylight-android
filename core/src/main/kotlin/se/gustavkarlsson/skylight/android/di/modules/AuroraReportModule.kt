package se.gustavkarlsson.skylight.android.di.modules

import io.reactivex.Flowable
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.services.providers.AuroraReportProvider

interface AuroraReportModule {
	val auroraReportProvider: AuroraReportProvider
	val auroraReportFlowable: Flowable<AuroraReport>
}
