package se.gustavkarlsson.skylight.android.feature.main.gui

import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import android.provider.Settings
import android.view.View
import androidx.annotation.StringRes
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Lifecycle
import com.jakewharton.rxbinding2.support.v7.widget.itemClicks
import com.jakewharton.rxbinding2.view.clicks
import com.uber.autodispose.LifecycleScopeProvider
import com.uber.autodispose.android.lifecycle.scope
import com.uber.autodispose.kotlin.autoDisposable
import io.reactivex.Observable
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
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import se.gustavkarlsson.skylight.android.feature.main.BuildConfig
import se.gustavkarlsson.skylight.android.feature.main.R
import se.gustavkarlsson.skylight.android.lib.permissions.PermissionRequester
import se.gustavkarlsson.skylight.android.lib.navigation.BackButtonHandler
import se.gustavkarlsson.skylight.android.lib.navigation.NavItem
import se.gustavkarlsson.skylight.android.lib.navigation.Navigator
import se.gustavkarlsson.skylight.android.lib.ui.BaseFragment
import timber.log.Timber
import kotlin.math.roundToInt


internal class MainFragment : BaseFragment(), BackButtonHandler {

	override val layoutId: Int = R.layout.fragment_main

	private var currentBottomSheetTitle: Int? = null

	private val viewModel: MainViewModel by viewModel()
	private val navigator: Navigator by inject()

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

	override fun onBackPressed(): Boolean =
		if (drawerLayout.isDrawerOpen(nav_view)) {
			drawerLayout.closeDrawer(nav_view)
			true
		} else {
			false
		}

	override fun bindData(scope: LifecycleScopeProvider<*>) {
		toolbarView.itemClicks()
			.autoDisposable(scope)
			.subscribe { item ->
				when (item.itemId) {
					R.id.action_settings -> navigator.push(NavItem("settings"))
					R.id.action_about -> navigator.push(NavItem("about"))
				}
			}

		viewModel.toolbarTitleText
			.doOnNext { Timber.d("Updating toolbar title: %s", it) }
			.autoDisposable(scope)
			.subscribe { toolbarView.title = it.resolve(requireContext()) }

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
		get<PermissionRequester>().request()
			.autoDisposable(scope(Lifecycle.Event.ON_DESTROY)) // Required to not dispose for dialog
			.subscribe()
	}

	private fun openAppDetails() {
		val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
		// FIXME Extract to module
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
		childFragmentManager.let {
			FactorBottomSheetDialogFragment.newInstance(
				title,
				description
			)
				.apply {
					onCancelListener = { currentBottomSheetTitle = null }
					show(it, javaClass.simpleName)
				}
			currentBottomSheetTitle = title
		}
	}

	private fun Observable<FactorItem>.present(
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
