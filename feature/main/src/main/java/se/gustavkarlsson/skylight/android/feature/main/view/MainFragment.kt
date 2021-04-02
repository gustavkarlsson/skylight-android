package se.gustavkarlsson.skylight.android.feature.main.view

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.AppBarDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.google.accompanist.insets.navigationBarsPadding
import com.ioki.textref.TextRef
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.launch
import se.gustavkarlsson.skylight.android.feature.main.viewmodel.BannerData
import se.gustavkarlsson.skylight.android.feature.main.viewmodel.Event
import se.gustavkarlsson.skylight.android.feature.main.viewmodel.MainViewModelComponent
import se.gustavkarlsson.skylight.android.feature.main.viewmodel.SearchViewState
import se.gustavkarlsson.skylight.android.feature.main.viewmodel.ViewState
import se.gustavkarlsson.skylight.android.lib.navigation.BackButtonHandler
import se.gustavkarlsson.skylight.android.lib.navigation.navigator
import se.gustavkarlsson.skylight.android.lib.navigation.screens
import se.gustavkarlsson.skylight.android.lib.permissions.PermissionsComponent
import se.gustavkarlsson.skylight.android.lib.scopedservice.getOrRegisterService
import se.gustavkarlsson.skylight.android.lib.ui.compose.Colors
import se.gustavkarlsson.skylight.android.lib.ui.compose.ComposeScreenFragment
import se.gustavkarlsson.skylight.android.lib.ui.compose.ScreenBackground
import se.gustavkarlsson.skylight.android.lib.ui.compose.ToggleButtonState
import se.gustavkarlsson.skylight.android.lib.ui.compose.textRef

@FlowPreview
@ExperimentalCoroutinesApi
class MainFragment : ComposeScreenFragment(), BackButtonHandler {

    private val viewModel by lazy {
        getOrRegisterService("mainViewModel") {
            MainViewModelComponent.build().viewModel()
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.onEvent(Event.RefreshLocationPermission)
    }

    @ExperimentalMaterialApi
    @ExperimentalAnimationApi
    @Composable
    override fun ScreenContent() {
        val state by viewModel.state.collectAsState()
        val scope = rememberCoroutineScope()
        val onBannerActionClicked: (BannerData.Event) -> Unit = { event ->
            when (event) {
                BannerData.Event.RequestLocationPermission -> requestLocationPermission(scope)
                BannerData.Event.OpenAppDetails -> openAppDetails()
            }
        }
        Content(
            state = state,
            onBannerActionClicked = onBannerActionClicked,
            onAboutClicked = { navigator.goTo(screens.about) },
            onEvent = { event -> viewModel.onEvent(event) },
        )
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

    override fun onBackPressed(): Boolean = viewModel.onBackPressed()
}

@ExperimentalMaterialApi
@ExperimentalAnimationApi
@Composable
@Preview
private fun PreviewContent() {
    Content(
        state = ViewState(
            toolbarTitleName = TextRef.EMPTY,
            chanceLevelText = TextRef.EMPTY,
            chanceSubtitleText = TextRef.EMPTY,
            errorBannerData = null,
            notificationsButtonState = ToggleButtonState.Enabled(checked = false),
            favoriteButtonState = ToggleButtonState.Enabled(checked = true),
            factorItems = emptyList(),
            search = SearchViewState.Closed,
            onFavoritesClickedEvent = Event.Noop,
            notificationLevelItems = emptyList(),
        ),
        onBannerActionClicked = {},
        onAboutClicked = {},
        onEvent = {},
    )
}

@ExperimentalMaterialApi
@ExperimentalAnimationApi
@Composable
private fun Content(
    state: ViewState,
    onBannerActionClicked: (BannerData.Event) -> Unit,
    onAboutClicked: () -> Unit,
    onEvent: (Event) -> Unit,
) {
    ScreenBackground {
        val topBarElevation = AppBarDefaults.TopAppBarElevation
        val topBarBackgroundColor = Colors.primarySurface
        Scaffold(
            topBar = {
                TopAppBar(
                    state = state.search,
                    title = textRef(state.toolbarTitleName),
                    onAboutClicked = onAboutClicked,
                    onEvent = onEvent,
                    backgroundColor = topBarBackgroundColor,
                    elevation = topBarElevation,
                )
            },
        ) {
            Box {
                SelectedPlace(
                    modifier = Modifier
                        .fillMaxSize()
                        .navigationBarsPadding(),
                    state = state,
                    onBannerActionClicked = onBannerActionClicked,
                    onEvent = onEvent,
                )
                SearchResults(
                    modifier = Modifier.fillMaxSize(),
                    state = state,
                    searchElevation = topBarElevation / 2,
                    searchBackgroundColor = topBarBackgroundColor,
                    onEvent = onEvent,
                )
            }
        }
    }
}
