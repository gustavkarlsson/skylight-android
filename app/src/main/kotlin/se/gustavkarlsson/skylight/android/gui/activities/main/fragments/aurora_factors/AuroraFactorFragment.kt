package se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_factors

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.dagger.FRAGMENT_ROOT_NAME
import se.gustavkarlsson.skylight.android.dagger.LATEST_NAME
import se.gustavkarlsson.skylight.android.dagger.modules.replaceable.FragmentRootViewModule
import se.gustavkarlsson.skylight.android.gui.activities.main.MainActivity
import se.gustavkarlsson.skylight.android.models.AuroraReport
import se.gustavkarlsson.skylight.android.observers.ObservableValue
import se.gustavkarlsson.skylight.android.observers.ValueObserver
import javax.inject.Inject
import javax.inject.Named

class AuroraFactorFragment : Fragment(), ValueObserver<AuroraReport> {

    @Inject
    @field:Named(FRAGMENT_ROOT_NAME)
    lateinit var rootView: View

    @Inject
	lateinit var geomagActivityPresenter: GeomagActivityPresenter

    @Inject
	lateinit var geomagLocationPresenter: GeomagLocationPresenter

    @Inject
	lateinit var visibilityPresenter: VisibilityPresenter

    @Inject
	lateinit var darknessPresenter: DarknessPresenter

    @Inject
    @field:Named(LATEST_NAME)
	lateinit var latestAuroraReport: ObservableValue<AuroraReport>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        (activity as MainActivity).component
                .getAuroraFactorsFragmentComponent(FragmentRootViewModule(inflater, container, R.layout.fragment_aurora_factors))
                .inject(this)
        return rootView
    }

    override fun onStart() {
        super.onStart()
        latestAuroraReport.addListener(this)
        updatePresenters(latestAuroraReport.value)
    }

    override fun valueChanged(newData: AuroraReport) {
        activity.runOnUiThread { updatePresenters(newData) }
    }

    private fun updatePresenters(report: AuroraReport) {
        val factors = report.factors
        geomagActivityPresenter.update(factors.geomagActivity)
        geomagLocationPresenter.update(factors.geomagLocation)
        visibilityPresenter.update(factors.visibility)
        darknessPresenter.update(factors.darkness)
    }

    override fun onStop() {
        latestAuroraReport.removeListener(this)
        super.onStop()
    }
}
