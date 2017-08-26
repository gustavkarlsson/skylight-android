package se.gustavkarlsson.skylight.android.dagger.modules

import dagger.Binds
import dagger.Module
import dagger.Reusable
import se.gustavkarlsson.skylight.android.actions.PresentingAuroraReports
import se.gustavkarlsson.skylight.android.actions_impl.aurora_reports.PresentingAuroraReportsFromStream

@Module
abstract class PresentingAuroraReportsModule {

	@Reusable
	@Binds
	abstract fun bindPresentingAuroraReportsFromStream(impl: PresentingAuroraReportsFromStream): PresentingAuroraReports
}
