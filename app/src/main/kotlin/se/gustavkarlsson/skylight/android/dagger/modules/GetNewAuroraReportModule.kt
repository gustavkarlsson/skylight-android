package se.gustavkarlsson.skylight.android.dagger.modules

import dagger.Binds
import dagger.Module
import dagger.Reusable
import se.gustavkarlsson.skylight.android.actions.GetNewAuroraReport
import se.gustavkarlsson.skylight.android.actions_impl.aurora_reports.GetNewAuroraReportUsingRx

@Module
abstract class GetNewAuroraReportModule {

	@Binds
	@Reusable
	abstract fun bindGetNewAuroraReport(impl: GetNewAuroraReportUsingRx): GetNewAuroraReport
}
