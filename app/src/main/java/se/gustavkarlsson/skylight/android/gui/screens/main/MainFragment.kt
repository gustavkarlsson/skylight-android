package se.gustavkarlsson.skylight.android.gui.screens.main

import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import android.provider.Settings
import android.view.View
import androidx.annotation.StringRes
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Lifecycle
import com.google.android.material.snackbar.Snackbar
import com.jakewharton.rxbinding2.support.v7.widget.itemClicks
import com.jakewharton.rxbinding2.view.clicks
import com.tbruyelle.rxpermissions2.RxPermissions
import com.uber.autodispose.LifecycleScopeProvider
import com.uber.autodispose.android.lifecycle.scope
import com.uber.autodispose.kotlin.autoDisposable
import io.reactivex.Flowable
import kotlinx.android.synthetic.main.fragment_main.chance
import kotlinx.android.synthetic.main.fragment_main.chanceSubtitle
import kotlinx.android.synthetic.main.fragment_main.darknessCard
import kotlinx.android.synthetic.main.fragment_main.drawerLayout
import kotlinx.android.synthetic.main.fragment_main.errorBanner
import kotlinx.android.synthetic.main.fragment_main.geomagLocationCard
import kotlinx.android.synthetic.main.fragment_main.kpIndexCard
import kotlinx.android.synthetic.main.fragment_main.nav_view
import kotlinx.android.synthetic.main.fragment_main.toolbarView
import kotlinx.android.synthetic.main.fragment_main.weatherCard
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import se.gustavkarlsson.skylight.android.BuildConfig
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.feature.base.BackButtonHandler
import se.gustavkarlsson.skylight.android.feature.base.BaseFragment
import se.gustavkarlsson.skylight.android.feature.base.Navigator
import se.gustavkarlsson.skylight.android.feature.base.doOnNext
import se.gustavkarlsson.skylight.android.feature.base.extensions.showErrorSnackbar
import se.gustavkarlsson.skylight.android.gui.views.FactorCard
import timber.log.Timber
import kotlin.math.roundToInt


class MainFragment : BaseFragment(), BackButtonHandler {

	override val layoutId: Int = R.layout.fragment_main

	private var currentBottomSheetTitle: Int? = null

	private val viewModel: MainViewModel by viewModel()
	private val navigator: Navigator by inject()
	private val locationPermission: String by inject("locationPermission")
	private val rxPermissions: RxPermissions by inject()

	override val toolbar: Toolbar?
		get() = toolbarView

	override fun onStart() {
		super.onStart()
		viewModel.refreshLocationPermission()
	}

	override fun initView() {
		toolbarView.enableNavigationDrawer()
		toolbarView.inflateMenu(R.menu.menu_main)
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
					R.id.action_settings -> navigator.navigate("settings")
					R.id.action_about -> navigator.navigate("about")
				}
			}

		viewModel.toolbarTitleText
			.doOnNext { Timber.d("Updating toolbar title: %s", it) }
			.autoDisposable(scope)
			.subscribe { toolbarView.title = it.resolve(requireContext()) }

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

		viewModel.chanceLevelText
			.doOnNext { Timber.d("Updating chanceLevel text: %s", it) }
			.map { it.resolve(requireContext()) }
			.autoDisposable(scope)
			.subscribe(chance::setText)

		viewModel.chanceSubtitleText
			.doOnNext { Timber.d("Updating chanceSubtitle text: %s", it) }
			.autoDisposable(scope)
			.subscribe { chanceSubtitle.text = it.resolve(requireContext()) }

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

		viewModel.errorBannerData
			.autoDisposable(scope)
			.subscribe { updateBanner(it.value) }

		viewModel.requestLocationPermission
			.autoDisposable(scope)
			.subscribe { requestLocationPermission() }

		viewModel.openAppDetails
			.autoDisposable(scope)
			.subscribe { openAppDetails() }
	}

	private fun updateBanner(data: BannerData?) {
		errorBanner.run {
			if (data != null) {
				setMessage(data.message.resolve(requireContext()))
				setRightButton(data.buttonText.resolve(requireContext())) { data.buttonAction() }
			}
			val isVisible = visibility == View.VISIBLE
			val shouldShow = data != null
			if (!isVisible && shouldShow)
				show()
			else if (isVisible && !shouldShow)
				dismiss()
		}
	}

	private fun requestLocationPermission() {
		rxPermissions
			.requestEach(locationPermission)
			.autoDisposable(scope(Lifecycle.Event.ON_DESTROY)) // Required to not dispose for dialog
			.subscribe {
				when {
					it.granted -> {
						Timber.i("Permission is granted")
						viewModel.refreshLocationPermission()
					}
					it.shouldShowRequestPermissionRationale -> {
						Timber.i("Permission is denied")
					}
					else -> {
						Timber.i("Permission is denied forever")
						viewModel.signalLocationPermissionDeniedForever()
					}
				}
			}
	}

	private fun openAppDetails() {
		val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
		intent.data = Uri.parse("package:${BuildConfig.APPLICATION_ID}")
		startActivity(intent)
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
