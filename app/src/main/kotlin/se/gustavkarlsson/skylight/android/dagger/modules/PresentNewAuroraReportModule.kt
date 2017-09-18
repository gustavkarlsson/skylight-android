package se.gustavkarlsson.skylight.android.dagger.modules

import dagger.Binds
import dagger.Module
import dagger.Reusable
import se.gustavkarlsson.skylight.android.actions.PresentNewAuroraReport
import se.gustavkarlsson.skylight.android.actions_impl.aurora_reports.ProvideNewAuroraReportToPublisher

@Module(includes = arrayOf(
		NewAuroraReportProviderModule::class,
		LatestAuroraReportStreamModule::class,
		UserFriendlyExceptionStreamModule::class
))
abstract class PresentNewAuroraReportModule {

	@Binds
	@Reusable
	abstract fun bindProvideNewAuroraReportToPublisher(impl: ProvideNewAuroraReportToPublisher): PresentNewAuroraReport
}
