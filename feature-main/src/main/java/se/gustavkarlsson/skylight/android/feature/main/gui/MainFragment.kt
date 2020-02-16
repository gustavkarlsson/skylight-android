package se.gustavkarlsson.skylight.android.feature.main.gui

import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import android.provider.Settings
import android.view.View
import androidx.annotation.StringRes
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.jakewharton.rxbinding2.support.v7.widget.itemClicks
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.Observable
import kotlinx.android.synthetic.main.fragment_main.*
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import se.gustavkarlsson.skylight.android.feature.main.R
import se.gustavkarlsson.skylight.android.lib.navigation.BackButtonHandler
import se.gustavkarlsson.skylight.android.lib.navigation.NavItem
import se.gustavkarlsson.skylight.android.lib.navigation.Navigator
import se.gustavkarlsson.skylight.android.lib.ui.BaseFragment
import se.gustavkarlsson.skylight.android.lib.ui.extensions.bind
import se.gustavkarlsson.skylight.android.services.PermissionRequester
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

	override fun bindData() {
		toolbarView.itemClicks()
			.bind(this) { item ->
				when (item.itemId) {
					R.id.action_settings -> navigator.push(NavItem("settings"))
					R.id.action_about -> navigator.push(NavItem("about"))
				}
			}

		viewModel.toolbarTitleText
			.doOnNext { Timber.d("Updating toolbar title: %s", it) }
			.bind(this) { toolbarView.title = it.resolve(requireContext()) }

		viewModel.chanceLevelText
			.doOnNext { Timber.d("Updating chanceLevel text: %s", it) }
			.map { it.resolve(requireContext()) }
			.bind(this, chance::setText)

		viewModel.chanceSubtitleText
			.doOnNext { Timber.d("Updating chanceSubtitle text: %s", it) }
			.bind(this) { chanceSubtitle.text = it.resolve(requireContext()) }

		viewModel.darkness.bindToCard(
			this,
			darknessCard,
			::showDarknessDetails,
			"darkness"
		)
		viewModel.geomagLocation.bindToCard(
			this,
			geomagLocationCard,
			::showGeomagLocationDetails,
			"geomagLocation"
		)
		viewModel.kpIndex.bindToCard(
			this,
			kpIndexCard,
			::showKpIndexDetails,
			"kpIndex"
		)
		viewModel.weather.bindToCard(
			this,
			weatherCard,
			::showWeatherDetails,
			"weather"
		)

		viewModel.errorBannerData.bind(this) { updateBanner(it.value) }
	}

	private fun updateBanner(data: BannerData?) {
		errorBanner.run {
			if (data != null) {
				setMessage(data.message.resolve(requireContext()))
				setRightButton(data.buttonText.resolve(requireContext())) {
					when (data.buttonEvent) {
						BannerData.Event.RequestLocationPermission -> requestLocationPermission()
						BannerData.Event.OpenAppDetails -> openAppDetails()
					}
				}
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
		get<PermissionRequester>().request().bind(this)
	}

	private fun openAppDetails() {
		val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
		intent.data = Uri.fromParts("package", requireContext().packageName, null)
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

	private fun Observable<FactorItem>.bindToCard(
		fragment: Fragment,
		cardView: FactorCard,
		onCardClick: () -> Unit,
		factorDebugName: String
	) {
		val maxProgress = (cardView.progressView.max * 0.97).roundToInt()
		val minProgress = (cardView.progressView.max - maxProgress)

		doOnNext { Timber.d("Updating %s factor card: %s", factorDebugName, it) }
			.bind(fragment) { item ->
				cardView.valueView.text = item.valueText.resolve(cardView.context)
				cardView.progressView.progressTintList = ColorStateList.valueOf(item.progressColor)
				cardView.progressView.progress = item.progress?.let { progressPercent ->
					(progressPercent * maxProgress).roundToInt() + minProgress
				} ?: 0
			}

		cardView.clicks()
			.bind(fragment) { onCardClick() }
	}
}
