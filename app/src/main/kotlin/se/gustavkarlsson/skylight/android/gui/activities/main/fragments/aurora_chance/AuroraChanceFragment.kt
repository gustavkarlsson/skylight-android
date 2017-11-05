package se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_chance

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_aurora_chance.*
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.dagger.FRAGMENT_ROOT_NAME
import se.gustavkarlsson.skylight.android.dagger.modules.FragmentRootViewModule
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.gui.activities.main.MainActivity
import se.gustavkarlsson.skylight.android.gui.viewmodels.AuroraReportViewModel
import se.gustavkarlsson.skylight.android.services.evaluation.ChanceEvaluator
import se.gustavkarlsson.skylight.android.services.evaluation.ChanceLevel
import javax.inject.Inject
import javax.inject.Named


class AuroraChanceFragment : Fragment() {

    @Inject
    @field:Named(FRAGMENT_ROOT_NAME)
    lateinit var rootView: View

	@Inject
	lateinit var timeSinceUpdateController: TimeSinceUpdateController

	@Inject
	lateinit var auroraReportViewModel: AuroraReportViewModel

	@Inject
	lateinit var auroraReportChanceEvaluator: ChanceEvaluator<AuroraReport>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        (activity as MainActivity).component
                .getAuroraChanceFragmentComponent(FragmentRootViewModule(inflater, container, R.layout.fragment_aurora_chance))
                .inject(this)
		bindData()
        return rootView
    }

	private fun bindData() {
		auroraReportViewModel.auroraReports.observe(this, Observer {
			it?.let {
				val chanceValue = auroraReportChanceEvaluator.evaluate(it) // Do we know this is not null?
				val chanceLevel = ChanceLevel.fromChance(chanceValue)
				chance.setText(chanceLevel.resourceId)
				timeSinceUpdateController.update(it.timestamp)
			}
		})
	}

	override fun onStart() {
        super.onStart()
        timeSinceUpdateController.start()
    }

    override fun onStop() {
        timeSinceUpdateController.stop()
        super.onStop()
    }
}
