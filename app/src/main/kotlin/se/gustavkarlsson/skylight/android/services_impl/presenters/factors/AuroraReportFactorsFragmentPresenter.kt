package se.gustavkarlsson.skylight.android.services_impl.presenters

import se.gustavkarlsson.skylight.android.entities.*
import se.gustavkarlsson.skylight.android.services.Presenter

class AuroraReportFactorsFragmentPresenter(
	val geomagActivityPresenter: Presenter<GeomagActivity>,
	val geomagLocationPresenter: Presenter<GeomagLocation>,
	val visibilityPresenter: Presenter<Visibility>,
	val darknessPresenter: Presenter<Darkness>
) : Presenter<AuroraReport> {

	override fun present(value: AuroraReport) {
		val factors = value.factors
		geomagActivityPresenter.present(factors.geomagActivity)
		geomagLocationPresenter.present(factors.geomagLocation)
		visibilityPresenter.present(factors.visibility)
		darknessPresenter.present(factors.darkness)
	}
}
