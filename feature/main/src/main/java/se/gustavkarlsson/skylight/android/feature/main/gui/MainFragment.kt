package se.gustavkarlsson.skylight.android.feature.main.gui

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.view.View
import androidx.annotation.StringRes
import com.google.android.material.appbar.MaterialToolbar
import com.ioki.textref.TextRef
import de.halfbit.edgetoedge.Edge
import de.halfbit.edgetoedge.EdgeToEdgeBuilder
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import reactivecircus.flowbinding.android.view.clicks
import reactivecircus.flowbinding.appcompat.itemClicks
import se.gustavkarlsson.skylight.android.core.logging.logDebug
import se.gustavkarlsson.skylight.android.feature.main.MainComponent
import se.gustavkarlsson.skylight.android.feature.main.R
import se.gustavkarlsson.skylight.android.lib.navigation.BackButtonHandler
import se.gustavkarlsson.skylight.android.lib.navigation.navigator
import se.gustavkarlsson.skylight.android.lib.navigation.screens
import se.gustavkarlsson.skylight.android.lib.permissions.PermissionsComponent
import se.gustavkarlsson.skylight.android.lib.scopedservice.getOrRegisterService
import se.gustavkarlsson.skylight.android.lib.ui.ScreenFragment
import se.gustavkarlsson.skylight.android.lib.ui.extensions.bind

@ExperimentalCoroutinesApi
class MainFragment : ScreenFragment(), BackButtonHandler {

    override val layoutId: Int = R.layout.fragment_main

    private var currentBottomSheetTitle: Int? = null

    private val viewModel by lazy {
        getOrRegisterService("mainViewModel") {
            MainComponent.build().viewModel()
        }
    }

    override val toolbar: MaterialToolbar get() = toolbarView

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

    private fun MaterialToolbar.enableNavigationDrawer() {
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

    override fun bindView(scope: CoroutineScope) {
        toolbarView.itemClicks().bind(scope) { item ->
            when (item.itemId) {
                R.id.action_settings -> navigator.goTo(screens.settings)
                R.id.action_about -> navigator.goTo(screens.about)
            }
        }

        viewModel.toolbarTitleText
            .onEach { logDebug { "Updating toolbar title: $it" } }
            .bind(scope) { toolbarView.title = it.resolve(requireContext()) }

        viewModel.chanceLevelText
            .onEach { logDebug { "Updating chanceLevel text: $it" } }
            .map { it.resolve(requireContext()) }
            .bind(scope) { text ->
                chance.text = text
            }

        viewModel.chanceSubtitleText
            .onEach { logDebug { "Updating chanceSubtitle text: $it" } }
            .bind(scope) { chanceSubtitle.text = it.resolve(requireContext()) }

        viewModel.darkness.bindToCard(
            scope,
            darknessCard,
            ::showDarknessDetails,
            "darkness"
        )

        viewModel.geomagLocation.bindToCard(
            scope,
            geomagLocationCard,
            ::showGeomagLocationDetails,
            "geomagLocation"
        )

        viewModel.kpIndex.bindToCard(
            scope,
            kpIndexCard,
            ::showKpIndexDetails,
            "kpIndex"
        )

        viewModel.weather.bindToCard(
            scope,
            weatherCard,
            ::showWeatherDetails,
            "weather"
        )

        viewModel.errorBannerData.bind(scope) { updateBanner(scope, it.value) }
    }

    private fun updateBanner(scope: CoroutineScope, data: BannerData?) {
        errorBanner.run {
            if (data != null) {
                setMessage(data.message.resolve(requireContext()))
                setRightButton(data.buttonText.resolve(requireContext())) {
                    when (data.buttonEvent) {
                        BannerData.Event.RequestLocationPermission -> requestLocationPermission(scope)
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

    private fun requestLocationPermission(scope: CoroutineScope) {
        scope.launch {
            PermissionsComponent.instance.locationPermissionRequester().request(this@MainFragment)
        }
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
                    show(it, javaClass.name)
                }
            currentBottomSheetTitle = title
        }
    }

    private fun Flow<FactorItem>.bindToCard(
        scope: CoroutineScope,
        cardView: FactorCard,
        onCardClick: (errorText: TextRef?) -> Unit,
        factorDebugName: String
    ) {
        onEach { logDebug { "Updating $factorDebugName factor card: $it" } }
            .bind(scope) { item ->
                cardView.setItem(item)
                cardView.clicks().bind(scope) {
                    onCardClick(item.errorText)
                }
            }
    }
}
