package se.gustavkarlsson.skylight.android.dagger.modules

import dagger.Binds
import dagger.Module
import dagger.Reusable
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.services.Presenter
import se.gustavkarlsson.skylight.android.services_impl.presenters.AuroraReportChanceFragmentPresenter

@Module
abstract class AuroraReportChancePresenterModule {

	@Binds
	@Reusable
	abstract fun bindAuroraReportChanceFragmentPresenter(impl: AuroraReportChanceFragmentPresenter): Presenter<AuroraReport>
}
