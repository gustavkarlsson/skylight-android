package se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_factors

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.actions.PresentingAuroraReports
import se.gustavkarlsson.skylight.android.dagger.FRAGMENT_ROOT_NAME
import se.gustavkarlsson.skylight.android.dagger.modules.FragmentRootViewModule
import se.gustavkarlsson.skylight.android.gui.activities.main.MainActivity
import javax.inject.Inject
import javax.inject.Named

class AuroraFactorFragment : Fragment() {

    @Inject
    @field:Named(FRAGMENT_ROOT_NAME)
    lateinit var rootView: View

	@Inject
	lateinit var presentingAuroraReports: PresentingAuroraReports

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        (activity as MainActivity).component
                .getAuroraFactorsFragmentComponent(FragmentRootViewModule(inflater, container, R.layout.fragment_aurora_factors))
                .inject(this)
        return rootView
    }

    override fun onStart() {
        super.onStart()
		presentingAuroraReports.start()
    }

    override fun onStop() {
		presentingAuroraReports.stop()
        super.onStop()
    }
}
