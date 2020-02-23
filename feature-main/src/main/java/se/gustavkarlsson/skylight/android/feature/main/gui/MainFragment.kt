package se.gustavkarlsson.skylight.android.feature.main.gui

import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import android.provider.Settings
import android.view.View
import androidx.annotation.StringRes
import androidx.appcompat.widget.Toolbar
import com.jakewharton.rxbinding2.support.v7.widget.itemClicks
import com.jakewharton.rxbinding2.view.clicks
import de.halfbit.edgetoedge.Edge
import de.halfbit.edgetoedge.EdgeToEdgeBuilder
import io.reactivex.Observable
import kotlinx.android.synthetic.main.fragment_main.*
import se.gustavkarlsson.skylight.android.appComponent
import se.gustavkarlsson.skylight.android.feature.main.MainComponent
import se.gustavkarlsson.skylight.android.feature.main.R
import se.gustavkarlsson.skylight.android.lib.scopedservice.getOrRegisterService
import se.gustavkarlsson.skylight.android.lib.ui.ScreenFragment
import se.gustavkarlsson.skylight.android.lib.ui.extensions.bind
import se.gustavkarlsson.skylight.android.lib.ui.extensions.toArgb
import se.gustavkarlsson.skylight.android.navigation.BackButtonHandler
import se.gustavkarlsson.skylight.android.navigation.navigator
import se.gustavkarlsson.skylight.android.navigation.screens
import timber.log.Timber
import kotlin.math.roundToInt

class MainFragment : ScreenFragment(), BackButtonHandler {

    override val layoutId: Int = R.layout.fragment_main

    private var currentBottomSheetTitle: Int? = null

    private val viewModel by lazy {
        getOrRegisterService("mainViewModel") {
            MainComponent.viewModel()
        }
    }

    override val toolbar: Toolbar?
        get() = toolbarView

    override fun onStart() {
        super.onStart()
        viewModel.refreshLocationPermission()
    }

    override fun setupEdgeToEdge(): EdgeToEdgeBuilder.() -> Unit = {
        toolbarView.fit { Edge.Top }
        darknessCard.fit { Edge.Bottom }
        navigationView.fit { Edge.Top + Edge.Bottom }
    }

    override fun initView() {
        toolbarView.enableNavigationDrawer()
        toolbarView.inflateMenu(R.menu.menu_main)
    }

    private fun Toolbar.enableNavigationDrawer() {
        setNavigationIcon(R.drawable.ic_menu)
        setNavigationOnClickListener {
            drawerLayout.openDrawer(navigationView)
        }
    }

    override fun onBackPressed(): Boolean =
        if (drawerLayout.isDrawerOpen(navigationView)) {
            drawerLayout.closeDrawer(navigationView)
            true
        } else {
            false
        }

    override fun bindData() {
        toolbarView.itemClicks()
            .bind(this) { item ->
                when (item.itemId) {
                    R.id.action_settings -> navigator.goTo(screens.settings)
                    R.id.action_about -> navigator.goTo(screens.about)
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
            darknessCard,
            ::showDarknessDetails,
            "darkness"
        )
        viewModel.geomagLocation.bindToCard(
            geomagLocationCard,
            ::showGeomagLocationDetails,
            "geomagLocation"
        )
        viewModel.kpIndex.bindToCard(
            kpIndexCard,
            ::showKpIndexDetails,
            "kpIndex"
        )
        viewModel.weather.bindToCard(
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
        appComponent.locationPermissionRequester().request(this).bind(this)
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

    private fun showFactorBottomSheetDialogFragment(
        @StringRes title: Int,
        @StringRes description: Int
    ) {
        if (currentBottomSheetTitle == title) return
        requireFragmentManager().let {
            FactorBottomSheetDialogFragment.newInstance(title, description)
                .apply {
                    onCancelListener = { currentBottomSheetTitle = null }
                    show(it, javaClass.simpleName)
                }
            currentBottomSheetTitle = title
        }
    }

    private fun Observable<FactorItem>.bindToCard(
        cardView: FactorCard,
        onCardClick: () -> Unit,
        factorDebugName: String
    ) {
        val maxProgress = (cardView.progressView.max * 0.97).roundToInt()
        val minProgress = (cardView.progressView.max - maxProgress)

        doOnNext { Timber.d("Updating %s factor card: %s", factorDebugName, it) }
            .bind(this@MainFragment) { item ->
                cardView.valueView.text = item.valueText.resolve(cardView.context)
                cardView.valueView.setTextColor(item.valueTextColor.toArgb(cardView.context))
                cardView.progressView.progressTintList = ColorStateList.valueOf(item.progressColor)
                cardView.progressView.progress = item.progress?.let { progressPercent ->
                    (progressPercent * maxProgress).roundToInt() + minProgress
                } ?: 0
            }

        cardView.clicks()
            .bind(this@MainFragment) { onCardClick() }
    }
}
