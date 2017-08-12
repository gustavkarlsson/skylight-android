package se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_factors

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.dagger.FRAGMENT_ROOT_NAME
import se.gustavkarlsson.skylight.android.dagger.modules.replaceable.FragmentRootViewModule
import se.gustavkarlsson.skylight.android.entities.*
import se.gustavkarlsson.skylight.android.gui.activities.main.MainActivity
import se.gustavkarlsson.skylight.android.services.Presenter
import javax.inject.Inject
import javax.inject.Named

class AuroraFactorFragment : Fragment() {

    @Inject
    @field:Named(FRAGMENT_ROOT_NAME)
    lateinit var rootView: View

    @Inject
	lateinit var geomagActivityPresenter: Presenter<GeomagActivity>

    @Inject
	lateinit var geomagLocationPresenter: Presenter<GeomagLocation>

    @Inject
	lateinit var visibilityPresenter: Presenter<Visibility>

    @Inject
	lateinit var darknessPresenter: Presenter<Darkness>

    @Inject
	lateinit var latestAuroraReports: Observable<AuroraReport>

	private var latestAuroraReportsSubscription: Disposable? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        (activity as MainActivity).component
                .getAuroraFactorsFragmentComponent(FragmentRootViewModule(inflater, container, R.layout.fragment_aurora_factors))
                .inject(this)
        return rootView
    }

    override fun onStart() {
        super.onStart()
		latestAuroraReportsSubscription = latestAuroraReports.subscribe { update(it) }
    }

    private fun update(report: AuroraReport) {
        val factors = report.factors
        geomagActivityPresenter.present(factors.geomagActivity)
        geomagLocationPresenter.present(factors.geomagLocation)
        visibilityPresenter.present(factors.visibility)
        darknessPresenter.present(factors.darkness)
    }

    override fun onStop() {
		latestAuroraReportsSubscription?.dispose()
        super.onStop()
    }
}
