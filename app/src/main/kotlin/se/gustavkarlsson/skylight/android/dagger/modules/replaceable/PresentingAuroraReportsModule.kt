package se.gustavkarlsson.skylight.android.dagger.modules.replaceable

import dagger.Module
import dagger.Provides
import dagger.Reusable
import se.gustavkarlsson.skylight.android.actions.PresentingAuroraReports
import se.gustavkarlsson.skylight.android.actions_impl.aurora_reports.PresentingAuroraReportsFromStream
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.services.Presenter
import se.gustavkarlsson.skylight.android.services.streams.Stream

@Module
class PresentingAuroraReportsModule {

	@Provides
	@Reusable
	fun providePresentingAuroraReports(errorStream: Stream<AuroraReport>, presenter: Presenter<AuroraReport>): PresentingAuroraReports {
		return PresentingAuroraReportsFromStream(errorStream, presenter)
	}
}
