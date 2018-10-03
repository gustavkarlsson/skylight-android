package se.gustavkarlsson.skylight.android.gui.screens.main

import android.os.Bundle
import androidx.annotation.StringRes
import androidx.appcompat.widget.PopupMenu
import com.google.android.material.snackbar.Snackbar
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.view.visibility
import com.jakewharton.rxbinding2.widget.text
import com.uber.autodispose.LifecycleScopeProvider
import com.uber.autodispose.kotlin.autoDisposable
import kotlinx.android.synthetic.main.fragment_main.chance
import kotlinx.android.synthetic.main.fragment_main.darknessBar
import kotlinx.android.synthetic.main.fragment_main.darknessCard
import kotlinx.android.synthetic.main.fragment_main.darknessValue
import kotlinx.android.synthetic.main.fragment_main.geomagLocationBar
import kotlinx.android.synthetic.main.fragment_main.geomagLocationCard
import kotlinx.android.synthetic.main.fragment_main.geomagLocationValue
import kotlinx.android.synthetic.main.fragment_main.kpIndexBar
import kotlinx.android.synthetic.main.fragment_main.kpIndexCard
import kotlinx.android.synthetic.main.fragment_main.kpIndexValue
import kotlinx.android.synthetic.main.fragment_main.locationName
import kotlinx.android.synthetic.main.fragment_main.menuButton
import kotlinx.android.synthetic.main.fragment_main.timeSinceUpdate
import kotlinx.android.synthetic.main.fragment_main.weatherBar
import kotlinx.android.synthetic.main.fragment_main.weatherCard
import kotlinx.android.synthetic.main.fragment_main.weatherValue
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.extensions.showErrorSnackbar
import se.gustavkarlsson.skylight.android.gui.BaseFragment
import se.gustavkarlsson.skylight.android.navigation.Navigator
import se.gustavkarlsson.skylight.android.navigation.Screen
import timber.log.Timber

class MainFragment : BaseFragment(R.layout.fragment_main) {

	private var errorSnackbar: Snackbar? = null
	private var currentBottomSheetTitle: Int? = null
	private lateinit var menu: PopupMenu

	private val viewModel: MainViewModel by viewModel()

	private val navigator: Navigator by inject()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setHasOptionsMenu(true)
	}

	override fun initView() {
		menu = PopupMenu(requireContext(), menuButton).apply {
			inflate(R.menu.menu_main)
			setOnMenuItemClickListener { item ->
				when (item.itemId) {
					R.id.action_settings -> {
						navigator.navigate(Screen.SETTINGS)
						true
					}
					R.id.action_about -> {
						navigator.navigate(Screen.ABOUT)
						true
					}
					else -> false
				}
			}
		}
	}

	override fun bindData(scope: LifecycleScopeProvider<*>) {
		menuButton.clicks()
			.autoDisposable(scope)
			.subscribe {
				menu.show()
			}

		viewModel.locationName
			.doOnNext { Timber.d("Updating locationName view: %s", it) }
			.autoDisposable(scope)
			.subscribe(locationName.text())

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
			darknessValue, darknessBar, darknessCard,
			::showDarknessDetails,
			scope,
			"darkness"
		).present()
		FactorPresenter(
			viewModel.geomagLocationValue, viewModel.geomagLocationChance,
			geomagLocationValue, geomagLocationBar, geomagLocationCard,
			::showGeomagLocationDetails,
			scope,
			"geomagLocation"
		).present()
		FactorPresenter(
			viewModel.kpIndexValue, viewModel.kpIndexChance,
			kpIndexValue, kpIndexBar, kpIndexCard,
			::showKpIndexDetails,
			scope,
			"kpIndex"
		).present()
		FactorPresenter(
			viewModel.weatherValue, viewModel.weatherChance,
			weatherValue, weatherBar, weatherCard,
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
