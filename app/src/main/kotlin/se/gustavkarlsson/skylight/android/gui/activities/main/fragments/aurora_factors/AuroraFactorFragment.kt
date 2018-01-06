package se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_factors

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_aurora_factors.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.okButton
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.dagger.FRAGMENT_ROOT_NAME
import se.gustavkarlsson.skylight.android.dagger.modules.FragmentRootViewModule
import se.gustavkarlsson.skylight.android.extensions.observe
import se.gustavkarlsson.skylight.android.gui.activities.main.MainActivity
import se.gustavkarlsson.skylight.android.gui.viewmodels.AuroraReportViewModel
import javax.inject.Inject
import javax.inject.Named

class AuroraFactorFragment : Fragment() {

    @Inject
    @field:Named(FRAGMENT_ROOT_NAME)
    lateinit var rootView: View

	@Inject
	lateinit var auroraReports: AuroraReportViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        (activity as MainActivity).component
                .getAuroraFactorsFragmentComponent(FragmentRootViewModule(inflater, container, R.layout.fragment_aurora_factors))
                .inject(this)
        return rootView
    }

	override fun onActivityCreated(savedInstanceState: Bundle?) {
		super.onActivityCreated(savedInstanceState)
		bindData()
	}

	private fun bindData() {
		auroraReports.kpIndex.observe(this) {
			kpIndex.value = it!!.value
			kpIndex.chance = it.chance
		}
		auroraReports.location.observe(this) {
			geomagLocation.value = it!!.value
			geomagLocation.chance = it.chance
		}
		auroraReports.visibility.observe(this) {
			visibility.value = it!!.value
			visibility.chance = it.chance
		}
		auroraReports.darkness.observe(this) {
			darkness.value = it!!.value
			darkness.chance = it.chance
		}

		kpIndex.setOnClickListener {
			toastFactor(R.string.factor_kp_index_title_full, R.string.factor_kp_index_desc)
		}

		geomagLocation.setOnClickListener {
			toastFactor(R.string.factor_geomag_location_title_full, R.string.factor_geomag_location_desc)
		}

		visibility.setOnClickListener {
			toastFactor(R.string.factor_visibility_title_full, R.string.factor_visibility_desc)
		}

		darkness.setOnClickListener {
			toastFactor(R.string.factor_darkness_title_full, R.string.factor_darkness_desc)
		}
	}

	private fun toastFactor( // TODO extract
		titleResourceId: Int,
		descriptionResourceId: Int) {
		context!!.alert {
			iconResource = R.drawable.info_white_24dp
			title = ctx.getString(titleResourceId)
			message = ctx.getString(descriptionResourceId)
			okButton { it.dismiss() }
		}.show()
	}
}
