package se.gustavkarlsson.skylight.android.dagger.modules

import dagger.Binds
import dagger.Module
import dagger.Reusable
import se.gustavkarlsson.skylight.android.actions.PresentNewAuroraReport
import se.gustavkarlsson.skylight.android.actions_impl.aurora_reports.ProvideNewAuroraReportToObserver

@Module
abstract class PresentNewAuroraReportModule {

	@Binds
	@Reusable
	abstract fun bindProvideNewAuroraReportToPublisher(impl: ProvideNewAuroraReportToObserver): PresentNewAuroraReport
}
