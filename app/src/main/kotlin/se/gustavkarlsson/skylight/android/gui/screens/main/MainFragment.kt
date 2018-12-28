package se.gustavkarlsson.skylight.android.gui.screens.main

import androidx.annotation.StringRes
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Lifecycle
import com.google.android.material.snackbar.Snackbar
import com.jakewharton.rxbinding2.support.v7.widget.itemClicks
import com.jakewharton.rxbinding2.view.visibility
import com.jakewharton.rxbinding2.widget.text
import com.uber.autodispose.LifecycleScopeProvider
import com.uber.autodispose.kotlin.autoDisposable
import kotlinx.android.synthetic.main.fragment_main.chance
import kotlinx.android.synthetic.main.fragment_main.darknessCard
import kotlinx.android.synthetic.main.fragment_main.drawerLayout
import kotlinx.android.synthetic.main.fragment_main.geomagLocationCard
import kotlinx.android.synthetic.main.fragment_main.kpIndexCard
import kotlinx.android.synthetic.main.fragment_main.nav_view
import kotlinx.android.synthetic.main.fragment_main.timeSinceUpdate
import kotlinx.android.synthetic.main.fragment_main.toolbarView
import kotlinx.android.synthetic.main.fragment_main.weatherCard
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.extensions.doOnNext
import se.gustavkarlsson.skylight.android.extensions.showErrorSnackbar
import se.gustavkarlsson.skylight.android.gui.BackButtonHandler
import se.gustavkarlsson.skylight.android.gui.BaseFragment
import se.gustavkarlsson.skylight.android.navigation.Navigator
import se.gustavkarlsson.skylight.android.navigation.Screen
import timber.log.Timber

class MainFragment : BaseFragment(R.layout.fragment_main), BackButtonHandler {

	private var currentBottomSheetTitle: Int? = null

	private val viewModel: MainViewModel by viewModel()
	private val navigator: Navigator by inject()

	override val toolbar: Toolbar?
		get() = toolbarView.apply {
			if (menu.size() == 0) {
				inflateMenu(R.menu.menu_main)
			}
		}

	override fun initView() {
		toolbarView.enableNavigationDrawer()
	}

	private fun Toolbar.enableNavigationDrawer() {
		setNavigationIcon(R.drawable.ic_menu_white_24dp)
		setNavigationOnClickListener {
			drawerLayout.openDrawer(nav_view)
		}
	}

	override fun onBackPressed(): Boolean {
		return if (drawerLayout.isDrawerOpen(nav_view)) {
			drawerLayout.closeDrawer(nav_view)
			true
		} else {
			false
		}
	}

	override fun bindData(scope: LifecycleScopeProvider<*>) {
		toolbarView.itemClicks()
			.autoDisposable(scope)
			.subscribe { item ->
				when (item.itemId) {
					R.id.action_settings -> navigator.navigate(Screen.SETTINGS)
					R.id.action_about -> navigator.navigate(Screen.ABOUT)
				}
			}

		viewModel.locationName
			.doOnNext { Timber.d("Updating locationName view: %s", it) }
			.autoDisposable(scope)
			.subscribe { toolbarView.title = it }

		viewModel.errorMessages
			.doOnNext { Timber.d("Showing error message") }
			.autoDisposable(scope)
			.subscribe {
				view?.let { view ->
					showErrorSnackbar(view, it, Snackbar.LENGTH_LONG)
						.doOnNext(this, Lifecycle.Event.ON_DESTROY) { snackbar ->
							snackbar.dismiss()
						}
				}
			}

		viewModel.chanceLevel
			.doOnNext { Timber.d("Updating chanceLevel view: %s", it) }
			.map { it.resolve(requireContext()) }
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
}
