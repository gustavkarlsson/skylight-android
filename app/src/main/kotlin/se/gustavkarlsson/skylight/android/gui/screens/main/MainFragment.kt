package se.gustavkarlsson.skylight.android.gui.screens.main

import android.os.Bundle
import androidx.annotation.StringRes
import com.google.android.material.snackbar.Snackbar
import com.jakewharton.rxbinding2.view.visibility
import com.jakewharton.rxbinding2.widget.text
import com.uber.autodispose.LifecycleScopeProvider
import com.uber.autodispose.kotlin.autoDisposable
import kotlinx.android.synthetic.main.fragment_main.chance
import kotlinx.android.synthetic.main.fragment_main.darknessCard
import kotlinx.android.synthetic.main.fragment_main.geomagLocationCard
import kotlinx.android.synthetic.main.fragment_main.kpIndexCard
import kotlinx.android.synthetic.main.fragment_main.timeSinceUpdate
import kotlinx.android.synthetic.main.fragment_main.weatherCard
import org.koin.androidx.viewmodel.ext.android.viewModel
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.extensions.showErrorSnackbar
import se.gustavkarlsson.skylight.android.gui.BaseFragment
import timber.log.Timber

class MainFragment : BaseFragment(R.layout.fragment_main, ToolbarConfig(null, R.menu.menu_main)) {

	private var errorSnackbar: Snackbar? = null
	private var currentBottomSheetTitle: Int? = null

	private val viewModel: MainViewModel by viewModel()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setHasOptionsMenu(true)
	}

	override fun bindData(scope: LifecycleScopeProvider<*>) {

		viewModel.locationName
			.doOnNext { Timber.d("Updating locationName view: %s", it) }
			.autoDisposable(scope)
			.subscribe {
				activityToolbar.title = it
			}

		viewModel.errorMessages
			.doOnNext { Timber.d("Showing error message") }
			.autoDisposable(scope)
			.subscribe {
				errorSnackbar?.run {
					Timber.d("Hiding previous error message")
					dismiss()
				}
				view?.let { view ->
					errorSnackbar =
						showErrorSnackbar(view, it, Snackbar.LENGTH_LONG).apply { show() }
				}
			}

		viewModel.chanceLevel
			.doOnNext { Timber.d("Updating chanceLevel view: %s", it) }
			.autoDisposable(scope)
			.subscribe(chance.text())

		viewModel.timeSinceUpdate
			.doOnNext { Timber.d("Updating timeSinceUpdate view: %s", it) }
			.autoDisposable(scope)
			.subscribe(timeSinceUpdate.text())

		viewModel.timeSinceUpdateVisibility
			.doOnNext { Timber.d("Updating timeSinceUpdate weather: %s", it) }
			.autoDisposable(scope)
			.subscribe(timeSinceUpdate.visibility())

		FactorPresenter(
			viewModel.darknessValue, viewModel.darknessChance,
			darknessCard,
			::showDarknessDetails,
			scope,
			"darkness"
		).present()
		FactorPresenter(
			viewModel.geomagLocationValue, viewModel.geomagLocationChance,
			geomagLocationCard,
			::showGeomagLocationDetails,
			scope,
			"geomagLocation"
		).present()
		FactorPresenter(
			viewModel.kpIndexValue, viewModel.kpIndexChance,
			kpIndexCard,
			::showKpIndexDetails,
			scope,
			"kpIndex"
		).present()
		FactorPresenter(
			viewModel.weatherValue, viewModel.weatherChance,
			weatherCard,
			::showWeatherDetails,
			scope,
			"weather"
		).present()
	}

	private fun showKpIndexDetails() {
		showFactorBottomSheetDialogFragment(
			R.string.factor_kp_index_title_full,
			R.string.factor_kp_index_desc
		)
	}

	private fun showGeomagLocationDetails() {
		showFactorBottomSheetDialogFragment(
			R.string.factor_geomag_location_title_full,
			R.string.factor_geomag_location_desc
		)
	}

	private fun showWeatherDetails() {
		showFactorBottomSheetDialogFragment(
			R.string.factor_weather_title_full,
			R.string.factor_weather_desc
		)
	}

	private fun showDarknessDetails() {
		showFactorBottomSheetDialogFragment(
			R.string.factor_darkness_title_full,
			R.string.factor_darkness_desc
		)
	}

	private fun showFactorBottomSheetDialogFragment(@StringRes title: Int, @StringRes description: Int) {
		if (currentBottomSheetTitle == title) return
		fragmentManager?.let {
			FactorBottomSheetDialogFragment
				.newInstance(title, description)
				.apply {
					onCancelListener = { currentBottomSheetTitle = null }
					show(it, javaClass.simpleName)
				}
			currentBottomSheetTitle = title
		}
	}

	override fun onDestroy() {
		errorSnackbar?.dismiss()
		super.onDestroy()
	}
}
