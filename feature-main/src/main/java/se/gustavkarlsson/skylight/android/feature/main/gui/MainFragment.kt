package se.gustavkarlsson.skylight.android.feature.main.gui

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.view.View
import androidx.annotation.StringRes
import androidx.appcompat.widget.Toolbar
import com.ioki.textref.TextRef
import com.jakewharton.rxbinding2.support.v7.widget.itemClicks
import com.jakewharton.rxbinding2.view.clicks
import de.halfbit.edgetoedge.Edge
import de.halfbit.edgetoedge.EdgeToEdgeBuilder
import io.reactivex.Observable
import kotlinx.android.synthetic.main.fragment_main.*
import se.gustavkarlsson.skylight.android.feature.main.MainComponent
import se.gustavkarlsson.skylight.android.feature.main.R
import se.gustavkarlsson.skylight.android.lib.navigation.BackButtonHandler
import se.gustavkarlsson.skylight.android.lib.navigation.navigator
import se.gustavkarlsson.skylight.android.lib.navigation.screens
import se.gustavkarlsson.skylight.android.lib.permissions.PermissionsComponent
import se.gustavkarlsson.skylight.android.lib.scopedservice.getOrRegisterService
import se.gustavkarlsson.skylight.android.lib.ui.ScreenFragment
import se.gustavkarlsson.skylight.android.lib.ui.extensions.bind
import timber.log.Timber

class MainFragment : ScreenFragment(), BackButtonHandler {

    override val layoutId: Int = R.layout.fragment_main

    private var currentBottomSheetTitle: Int? = null

    private val viewModel by lazy {
        getOrRegisterService("mainViewModel") {
            MainComponent.build().viewModel()
        }
    }

    override val toolbar: Toolbar?
        get() = toolbarView

    override fun onStart() {
        super.onStart()
        viewModel.resumeStreaming()
        viewModel.refreshLocationPermission()
    }

    override fun onStop() {
        viewModel.pauseStreaming()
        super.onStop()
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
        PermissionsComponent.instance.locationPermissionRequester().request(this)
            .bind(this)
    }

    private fun openAppDetails() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Uri.fromParts("package", requireContext().packageName, null)
        startActivity(intent)
    }

    private fun showKpIndexDetails(errorText: TextRef?) {
        showFactorBottomSheetDialogFragment(
            R.string.factor_kp_index_title_full,
            R.string.factor_kp_index_desc,
            errorText
        )
    }

    private fun showGeomagLocationDetails(errorText: TextRef?) {
        showFactorBottomSheetDialogFragment(
            R.string.factor_geomag_location_title_full,
            R.string.factor_geomag_location_desc,
            errorText
        )
    }

    private fun showWeatherDetails(errorText: TextRef?) {
        showFactorBottomSheetDialogFragment(
            R.string.factor_weather_title_full,
            R.string.factor_weather_desc,
            errorText
        )
    }

    private fun showDarknessDetails(errorText: TextRef?) {
        showFactorBottomSheetDialogFragment(
            R.string.factor_darkness_title_full,
            R.string.factor_darkness_desc,
            errorText
        )
    }

    private fun showFactorBottomSheetDialogFragment(
        @StringRes title: Int,
        @StringRes description: Int,
        errorText: TextRef?
    ) {
        if (currentBottomSheetTitle == title) return
        parentFragmentManager.let {
            val resolvedErrorText = errorText?.resolve(requireContext())
            FactorBottomSheetDialogFragment.newInstance(title, description, resolvedErrorText)
                .apply {
                    onCancelListener = { currentBottomSheetTitle = null }
                    show(it, javaClass.simpleName)
                }
            currentBottomSheetTitle = title
        }
    }

    private fun Observable<FactorItem>.bindToCard(
        fragment: MainFragment,
        cardView: FactorCard,
        onCardClick: (errorText: TextRef?) -> Unit,
        factorDebugName: String
    ) {
        doOnNext { Timber.d("Updating %s factor card: %s", factorDebugName, it) }
            .bind(fragment) { item ->
                cardView.setItem(item)
                cardView.clicks().bind(fragment) {
                    onCardClick(item.errorText)
                }
            }
    }
}
