package se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_chance

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.dagger.FRAGMENT_ROOT_NAME
import se.gustavkarlsson.skylight.android.dagger.LATEST_NAME
import se.gustavkarlsson.skylight.android.dagger.modules.replaceable.FragmentRootViewModule
import se.gustavkarlsson.skylight.android.gui.activities.main.MainActivity
import se.gustavkarlsson.skylight.android.models.AuroraReport
import javax.inject.Inject
import javax.inject.Named

class AuroraChanceFragment : Fragment() {

    @Inject
    @field:Named(FRAGMENT_ROOT_NAME)
    lateinit var rootView: View

    @Inject
	lateinit var locationPresenter: LocationPresenter

    @Inject
	lateinit var chancePresenter: ChancePresenter

	@Inject
	lateinit var timeSinceUpdateController: TimeSinceUpdateController

    @Inject
    @field:Named(LATEST_NAME)
	lateinit var latestAuroraReport: Observable<AuroraReport>

	private var auroraSubscription: Disposable? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        (activity as MainActivity).component
                .getAuroraChanceFragmentComponent(FragmentRootViewModule(inflater, container, R.layout.fragment_aurora_chance))
                .inject(this)
        return rootView
    }

    override fun onStart() {
        super.onStart()
		auroraSubscription = latestAuroraReport.subscribe { update(it) }
        timeSinceUpdateController.start()
    }

    private fun update(report: AuroraReport) {
        locationPresenter.present(report.address)
        chancePresenter.present(report)
		timeSinceUpdateController.update(report.timestamp)
    }

    override fun onStop() {
		auroraSubscription?.dispose()
        timeSinceUpdateController.stop()
        super.onStop()
    }
}
