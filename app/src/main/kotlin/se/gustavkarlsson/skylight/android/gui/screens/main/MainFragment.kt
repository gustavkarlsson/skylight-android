package se.gustavkarlsson.skylight.android.gui.screens.main

import android.content.res.ColorStateList
import androidx.annotation.StringRes
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Lifecycle
import com.google.android.material.snackbar.Snackbar
import com.jakewharton.rxbinding2.support.v7.widget.itemClicks
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.view.visibility
import com.uber.autodispose.LifecycleScopeProvider
import com.uber.autodispose.kotlin.autoDisposable
import io.reactivex.Flowable
import kotlinx.android.synthetic.main.fragment_main.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.extensions.doOnNext
import se.gustavkarlsson.skylight.android.extensions.showErrorSnackbar
import se.gustavkarlsson.skylight.android.gui.BaseFragment
import se.gustavkarlsson.skylight.android.gui.views.FactorCard
import se.gustavkarlsson.skylight.android.navigation.Navigator
import se.gustavkarlsson.skylight.android.navigation.Screen
import timber.log.Timber
import kotlin.math.roundToInt

class MainFragment : BaseFragment(R.layout.fragment_main) {

	private var currentBottomSheetTitle: Int? = null

	private val viewModel: MainViewModel by viewModel()
	private val navigator: Navigator by inject()

	override fun getToolbar(): Toolbar? {
		return toolbar.apply {
			inflateMenu(R.menu.menu_main)
		}
	}

	override fun bindData(scope: LifecycleScopeProvider<*>) {
		toolbar.itemClicks()
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
			.subscribe { toolbar.title = it }

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
			.subscribe(chance::setText)

		viewModel.timeSinceUpdate
			.doOnNext { Timber.d("Updating timeSinceUpdate view: %s", it) }
			.autoDisposable(scope)
			.subscribe(timeSinceUpdate::setText)

		viewModel.timeSinceUpdateVisibility
			.doOnNext { Timber.d("Updating timeSinceUpdate weather: %s", it) }
			.autoDisposable(scope)
			.subscribe(timeSinceUpdate.visibility())

		viewModel.darkness.present(
			darknessCard,
			::showDarknessDetails,
			scope,
			"darkness"
		)
		viewModel.geomagLocation.present(
			geomagLocationCard,
			::showGeomagLocationDetails,
			scope,
			"geomagLocation"
		)
		viewModel.kpIndex.present(
			kpIndexCard,
			::showKpIndexDetails,
			scope,
			"kpIndex"
		)
		viewModel.weather.present(
			weatherCard,
			::showWeatherDetails,
			scope,
			"weather"
		)
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

	private fun Flowable<FactorItem>.present(
		cardView: FactorCard,
		onCardClick: () -> Unit,
		scope: LifecycleScopeProvider<*>,
		factorDebugName: String
	) {
		val maxProgress = (cardView.progressView.max * 0.97).roundToInt()
		val minProgress = (cardView.progressView.max - maxProgress)

		doOnNext { Timber.d("Updating %s factor card: %s", factorDebugName, it) }
			.autoDisposable(scope)
			.subscribe { item ->
				cardView.valueView.text = item.valueText.resolve(cardView.context)
				cardView.progressView.progressTintList = ColorStateList.valueOf(item.progressColor)
				cardView.progressView.progress = item.progress?.let { progressPercent ->
					(progressPercent * maxProgress).roundToInt() + minProgress
				} ?: 0
			}

		cardView.clicks()
			.autoDisposable(scope)
			.subscribe { onCardClick() }
	}
}
