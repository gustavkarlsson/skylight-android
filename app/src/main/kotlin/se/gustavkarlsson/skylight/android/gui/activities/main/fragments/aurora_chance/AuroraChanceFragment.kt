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
import se.gustavkarlsson.skylight.android.dagger.modules.replaceable.FragmentRootViewModule
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.gui.activities.main.MainActivity
import se.gustavkarlsson.skylight.android.services.Presenter
import se.gustavkarlsson.skylight.android.services.evaluation.Chance
import se.gustavkarlsson.skylight.android.services.evaluation.ChanceEvaluator
import javax.inject.Inject
import javax.inject.Named

class AuroraChanceFragment : Fragment() {

    @Inject
    @field:Named(FRAGMENT_ROOT_NAME)
    lateinit var rootView: View

	@Inject
	lateinit var auroraChanceEvaluator: ChanceEvaluator<AuroraReport>

    @Inject
	lateinit var locationPresenter: Presenter<String?>

    @Inject
	lateinit var chancePresenter: Presenter<Chance>

	@Inject
	lateinit var timeSinceUpdateController: TimeSinceUpdateController

    @Inject
	lateinit var latestAuroraReports: Observable<AuroraReport>

	private var latestAuroraReportsSubscription: Disposable? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        (activity as MainActivity).component
                .getAuroraChanceFragmentComponent(FragmentRootViewModule(inflater, container, R.layout.fragment_aurora_chance))
                .inject(this)
        return rootView
    }

    override fun onStart() {
        super.onStart()
		latestAuroraReportsSubscription = latestAuroraReports.subscribe { update(it) }
        timeSinceUpdateController.start()
    }

    private fun update(report: AuroraReport) {
        locationPresenter.present(report.location)
        chancePresenter.present(auroraChanceEvaluator.evaluate(report))
		timeSinceUpdateController.update(report.timestamp)
    }

    override fun onStop() {
		latestAuroraReportsSubscription?.dispose()
        timeSinceUpdateController.stop()
        super.onStop()
    }
}
