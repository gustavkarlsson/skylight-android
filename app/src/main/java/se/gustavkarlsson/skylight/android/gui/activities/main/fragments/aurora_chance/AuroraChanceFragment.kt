package se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_chance

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.threeten.bp.Instant
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.dagger.Names.FRAGMENT_ROOT_NAME
import se.gustavkarlsson.skylight.android.dagger.Names.LATEST_NAME
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
	lateinit var timeSinceUpdatePresenter: TimeSinceUpdatePresenter

    @Inject
	lateinit var chancePresenter: ChancePresenter

    @Inject
    @field:Named(LATEST_NAME)
	lateinit var latestAuroraReport: ObservableValue<AuroraReport>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.v(TAG, "onCreateView")
        (activity as MainActivity).component
                .getAuroraChanceFragmentComponent(FragmentRootViewModule(inflater, container, R.layout.fragment_aurora_chance))
                .inject(this)
        return rootView
    }

    override fun onStart() {
        Log.v(TAG, "onStart")
        super.onStart()
        latestAuroraReport.addListener(this)
        updatePresenters(latestAuroraReport.value)
        timeSinceUpdatePresenter.start()
    }

    override fun valueChanged(newData: AuroraReport) {
        Log.v(TAG, "valueChanged")
        activity.runOnUiThread { updatePresenters(newData) }
    }

    private fun updatePresenters(report: AuroraReport) {
        locationPresenter.update(report.address)
        timeSinceUpdatePresenter.update(Instant.ofEpochMilli(report.timestampMillis))
        chancePresenter.update(report)
    }

    override fun onStop() {
        Log.v(TAG, "onStop")
        latestAuroraReport.removeListener(this)
        timeSinceUpdatePresenter.stop()
        super.onStop()
    }

    companion object {
        private val TAG = AuroraChanceFragment::class.java.simpleName
    }
}
