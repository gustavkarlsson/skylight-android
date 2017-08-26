package se.gustavkarlsson.skylight.android.dagger.modules

import dagger.Binds
import dagger.Module
import dagger.Reusable
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.services.Presenter
import se.gustavkarlsson.skylight.android.services_impl.presenters.AuroraReportFactorsFragmentPresenter

@Module
abstract class AuroraReportFactorsPresenterModule {

	@Binds
	@Reusable
	abstract fun bindAuroraReportFactorsFragmentPresenter(impl: AuroraReportFactorsFragmentPresenter): Presenter<AuroraReport>
}
