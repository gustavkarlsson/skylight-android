package se.gustavkarlsson.skylight.android.dagger.modules.replaceable

import dagger.Module
import dagger.Provides
import dagger.Reusable
import se.gustavkarlsson.skylight.android.entities.*
import se.gustavkarlsson.skylight.android.services.Presenter
import se.gustavkarlsson.skylight.android.services_impl.presenters.AuroraReportFactorsPresenter

@Module
class AuroraReportFactorsPresenterModule {

    // Published
	@Provides
	@Reusable
	fun provideAuroraReportsPresenter(geomagActivityPresenter: Presenter<GeomagActivity>, geomagLocationPresenter: Presenter<GeomagLocation>, visibilityPresenter: Presenter<Visibility>, darknessPresenter: Presenter<Darkness>): Presenter<AuroraReport> {
		return AuroraReportFactorsPresenter(geomagActivityPresenter, geomagLocationPresenter, visibilityPresenter, darknessPresenter)
	}
}
