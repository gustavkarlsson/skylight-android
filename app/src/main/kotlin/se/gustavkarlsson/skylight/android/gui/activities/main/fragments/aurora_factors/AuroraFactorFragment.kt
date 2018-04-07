package se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_factors

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_aurora_factors.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.okButton
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.appComponent
import se.gustavkarlsson.skylight.android.gui.AutoDisposingFragment
import timber.log.Timber

class AuroraFactorFragment : AutoDisposingFragment() {

	private val factory: AuroraFactorsViewModelFactory by lazy {
		appComponent.getAuroraFactorsViewModelFactory()
	}

	private val darknessViewModel: DarknessViewModel by lazy {
		ViewModelProviders.of(this, factory).get(DarknessViewModel::class.java)
	}

	private val geomagLocationViewModel: GeomagLocationViewModel by lazy {
		ViewModelProviders.of(this, factory).get(GeomagLocationViewModel::class.java)
	}

	private val kpIndexViewModel: KpIndexViewModel by lazy {
		ViewModelProviders.of(this, factory).get(KpIndexViewModel::class.java)
	}

	private val visibilityViewModel: VisibilityViewModel by lazy {
		ViewModelProviders.of(this, factory).get(VisibilityViewModel::class.java)
	}

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		return inflater.inflate(R.layout.fragment_aurora_factors, container)
	}

	override fun onStart() {
		super.onStart()
		bindData()
	}

	private fun bindData() {
		bindFactor(
			darknessViewModel, darkness,
			R.string.factor_darkness_title_full, R.string.factor_darkness_desc, "darkness"
		)
		bindFactor(
			geomagLocationViewModel,
			geomagLocation,
			R.string.factor_geomag_location_title_full,
			R.string.factor_geomag_location_desc,
			"geomagLocation"
		)
		bindFactor(
			kpIndexViewModel, kpIndex,
			R.string.factor_kp_index_title_full, R.string.factor_kp_index_desc, "kpIndex"
		)
		bindFactor(
			visibilityViewModel, visibility,
			R.string.factor_visibility_title_full, R.string.factor_visibility_desc, "visibility"
		)
	}

	private fun bindFactor(
		viewModel: FactorViewModel<*>,
		view: AuroraFactorView,
		titleResourceId: Int,
		descriptionResourceId: Int,
		factorDebugName: String
	) {
		viewModel.value
			.doOnNext { Timber.d("Updating %s value view: %s", factorDebugName, it) }
			.observeOn(AndroidSchedulers.mainThread())
			.subscribe { view.value = it }
			.autoDisposeOnStop()

		viewModel.chance
			.doOnNext { Timber.d("Updating %s chance view: %s", factorDebugName, it) }
			.observeOn(AndroidSchedulers.mainThread())
			.subscribe { view.chance = it }
			.autoDisposeOnStop()

		view.clicks()
			.observeOn(AndroidSchedulers.mainThread())
			.subscribe {
				toastFactorInfo(titleResourceId, descriptionResourceId)
			}
			.autoDisposeOnStop()
	}

	private fun toastFactorInfo(
		titleResourceId: Int,
		descriptionResourceId: Int
	) {
		context?.alert {
			iconResource = R.drawable.info_white_24dp
			title = ctx.getString(titleResourceId)
			message = ctx.getString(descriptionResourceId)
			okButton { it.dismiss() }
		}?.show()
	}
}
