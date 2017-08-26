package se.gustavkarlsson.skylight.android.services_impl.presenters

import dagger.Reusable
import se.gustavkarlsson.skylight.android.entities.*
import se.gustavkarlsson.skylight.android.services.Presenter
import javax.inject.Inject

@Reusable
class AuroraReportFactorsFragmentPresenter
@Inject
constructor(
        private val geomagActivityPresenter: Presenter<GeomagActivity>,
        private val geomagLocationPresenter: Presenter<GeomagLocation>,
        private val visibilityPresenter: Presenter<Visibility>,
        private val darknessPresenter: Presenter<Darkness>
) : Presenter<AuroraReport> {

    override fun present(value: AuroraReport) {
        val factors = value.factors
        geomagActivityPresenter.present(factors.geomagActivity)
        geomagLocationPresenter.present(factors.geomagLocation)
        visibilityPresenter.present(factors.visibility)
        darknessPresenter.present(factors.darkness)
    }
}
