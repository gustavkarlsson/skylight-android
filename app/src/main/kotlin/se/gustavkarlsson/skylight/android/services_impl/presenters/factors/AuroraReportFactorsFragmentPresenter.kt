package se.gustavkarlsson.skylight.android.services_impl.presenters.factors

import dagger.Reusable
import se.gustavkarlsson.skylight.android.entities.*
import se.gustavkarlsson.skylight.android.services.Presenter
import javax.inject.Inject

@Reusable
class AuroraReportFactorsFragmentPresenter
@Inject
constructor(
	private val kpIndexPresenter: Presenter<KpIndex>,
	private val geomagLocationPresenter: Presenter<GeomagLocation>,
	private val visibilityPresenter: Presenter<Visibility>,
	private val darknessPresenter: Presenter<Darkness>
) : Presenter<AuroraReport> {

    override fun present(value: AuroraReport) {
        val factors = value.factors
        kpIndexPresenter.present(factors.kpIndex)
        geomagLocationPresenter.present(factors.geomagLocation)
        visibilityPresenter.present(factors.visibility)
        darknessPresenter.present(factors.darkness)
    }
}
