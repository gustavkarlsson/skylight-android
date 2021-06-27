package se.gustavkarlsson.skylight.android.feature.main.view

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.FragmentActivity
import com.google.accompanist.insets.navigationBarsPadding
import com.ioki.textref.TextRef
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.parcelize.Parcelize
import se.gustavkarlsson.skylight.android.feature.main.viewmodel.BannerData
import se.gustavkarlsson.skylight.android.feature.main.viewmodel.Event
import se.gustavkarlsson.skylight.android.feature.main.viewmodel.MainViewModel
import se.gustavkarlsson.skylight.android.feature.main.viewmodel.MainViewModelComponent
import se.gustavkarlsson.skylight.android.feature.main.viewmodel.SearchViewState
import se.gustavkarlsson.skylight.android.feature.main.viewmodel.ViewState
import se.gustavkarlsson.skylight.android.lib.navigation.BackPress
import se.gustavkarlsson.skylight.android.lib.navigation.Screen
import se.gustavkarlsson.skylight.android.lib.navigation.ScreenName
import se.gustavkarlsson.skylight.android.lib.navigation.navigator
import se.gustavkarlsson.skylight.android.lib.navigation.screens
import se.gustavkarlsson.skylight.android.lib.permissions.Permission
import se.gustavkarlsson.skylight.android.lib.permissions.PermissionsComponent
import se.gustavkarlsson.skylight.android.lib.ui.compose.ScreenBackground
import se.gustavkarlsson.skylight.android.lib.ui.compose.SearchFieldState
import se.gustavkarlsson.skylight.android.lib.ui.compose.ToggleButtonState
import se.gustavkarlsson.skylight.android.lib.ui.compose.textRef
import se.gustavkarlsson.skylight.android.lib.ui.getOrRegisterService
import se.gustavkarlsson.skylight.android.lib.ui.startStopScope

@Parcelize
object MainScreen : Screen {
    override val name get() = ScreenName.Main

    override val scopeStart get() = "main"

    private val AppCompatActivity.viewModel: MainViewModel
        get() = getOrRegisterService(this@MainScreen, "mainViewModel") {
            MainViewModelComponent.build().viewModel()
        }

    override fun AppCompatActivity.onStartStopScope(scope: CoroutineScope) {
        viewModel.onEvent(Event.RefreshLocationPermission)
    }

    override fun AppCompatActivity.onBackPress(): BackPress {
        return when (viewModel.state.value.search) {
            SearchViewState.Closed -> BackPress.NOT_HANDLED
            is SearchViewState.Open -> {
                viewModel.onEvent(Event.SearchChanged(SearchFieldState.Inactive))
                BackPress.HANDLED
            }
        }
    }

    private fun Activity.openAppDetails() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Uri.fromParts("package", packageName, null)
        startActivity(intent)
    }

    private fun FragmentActivity.requestPermission(permission: Permission) {
        startStopScope?.launch {
            withContext(Dispatchers.Main) {
                PermissionsComponent.instance.permissionRequester()
                    .request(this@requestPermission, permission)
            }
        }
    }

    @Composable
    override fun AppCompatActivity.Content(scope: CoroutineScope) {
        val viewModel = getOrRegisterService(this@MainScreen, "mainViewModel") {
            MainViewModelComponent.build().viewModel()
        }
        val state by viewModel.state.collectAsState()
        Content(
            state = state,
            onBannerActionClicked = { event ->
                when (event) {
                    BannerData.Event.RequestLocationPermission ->
                        requestPermission(Permission.Location)
                    BannerData.Event.RequestBackgroundLocationPermission ->
                        requestPermission(Permission.BackgroundLocation)
                    BannerData.Event.OpenAppDetails ->
                        openAppDetails()
                }
            },
            onAboutClicked = { navigator.goTo(screens.about) },
            onEvent = { event -> viewModel.onEvent(event) },
        )
    }
}

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

@Composable
private fun Content(
    state: ViewState,
    onBannerActionClicked: (BannerData.Event) -> Unit,
    onAboutClicked: () -> Unit,
    onEvent: (Event) -> Unit,
) {
    ScreenBackground {
        Scaffold(
            topBar = {
                TopAppBar(
                    state = state.search,
                    title = textRef(state.toolbarTitleName),
                    onAboutClicked = onAboutClicked,
                    onEvent = onEvent,
                )
            },
        ) { paddingValues ->
            Crossfade(
                modifier = Modifier.padding(paddingValues),
                targetState = state.search is SearchViewState.Open,
            ) { showSearch ->
                if (showSearch) {
                    SearchResults(
                        modifier = Modifier.fillMaxSize(),
                        state = state,
                        onEvent = onEvent,
                    )
                } else {
                    SelectedPlace(
                        modifier = Modifier
                            .fillMaxSize()
                            .navigationBarsPadding(),
                        state = state,
                        onBannerActionClicked = onBannerActionClicked,
                        onEvent = onEvent,
                    )
                }
            }
        }
    }
}
