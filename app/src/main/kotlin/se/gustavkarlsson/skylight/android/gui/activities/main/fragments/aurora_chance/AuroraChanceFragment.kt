package se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_chance

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

class AuroraChanceFragment : Fragment(), ValueObserver<AuroraReport> {

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
	lateinit var latestAuroraReport: ObservableValue<AuroraReport>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        (activity as MainActivity).component
                .getAuroraChanceFragmentComponent(FragmentRootViewModule(inflater, container, R.layout.fragment_aurora_chance))
                .inject(this)
        return rootView
    }

    override fun onStart() {
        super.onStart()
        latestAuroraReport.addListener(this)
        update(latestAuroraReport.value)
        timeSinceUpdateController.start()
    }

    override fun valueChanged(newData: AuroraReport) = update(newData)

    private fun update(report: AuroraReport) {
        locationPresenter.present(report.address)
        chancePresenter.present(report)
		timeSinceUpdateController.update(report.timestamp)
    }

    override fun onStop() {
        latestAuroraReport.removeListener(this)
        timeSinceUpdateController.stop()
        super.onStop()
    }
}
