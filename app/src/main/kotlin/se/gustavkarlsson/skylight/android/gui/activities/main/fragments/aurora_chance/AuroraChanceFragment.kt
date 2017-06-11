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

// TODO separate presentation and timer
class AuroraChanceFragment : Fragment(), ValueObserver<AuroraReport> {

    @Inject
    @field:Named(FRAGMENT_ROOT_NAME)
    lateinit var rootView: View

    @Inject
	lateinit var locationPresenter: LocationPresenter

    @Inject
	lateinit var timeSinceUpdatePresenter: TimeSinceUpdatePresenter

    @Inject
	lateinit var chancePresenter: ChancePresenter

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
        present(latestAuroraReport.value)
        timeSinceUpdatePresenter.start()
    }

    override fun valueChanged(newData: AuroraReport) = present(newData)

    private fun present(report: AuroraReport) {
        locationPresenter.present(report.address)
        timeSinceUpdatePresenter.present(report.timestamp)
        chancePresenter.present(report)
    }

    override fun onStop() {
        latestAuroraReport.removeListener(this)
        timeSinceUpdatePresenter.stop()
        super.onStop()
    }
}
