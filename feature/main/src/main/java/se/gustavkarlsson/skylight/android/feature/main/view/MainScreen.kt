package se.gustavkarlsson.skylight.android.feature.main.view

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.FragmentActivity
import app.cash.exhaustive.Exhaustive
import com.ioki.textref.TextRef
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.parcelize.Parcelize
import se.gustavkarlsson.skylight.android.feature.main.viewmodel.AppBarState
import se.gustavkarlsson.skylight.android.feature.main.viewmodel.BannerData
import se.gustavkarlsson.skylight.android.feature.main.viewmodel.ContentState
import se.gustavkarlsson.skylight.android.feature.main.viewmodel.Event
import se.gustavkarlsson.skylight.android.feature.main.viewmodel.MainViewModel
import se.gustavkarlsson.skylight.android.feature.main.viewmodel.MainViewModelComponent
import se.gustavkarlsson.skylight.android.feature.main.viewmodel.ViewState
import se.gustavkarlsson.skylight.android.lib.navigation.BackPress
import se.gustavkarlsson.skylight.android.lib.navigation.Screen
import se.gustavkarlsson.skylight.android.lib.navigation.ScreenName
import se.gustavkarlsson.skylight.android.lib.navigation.navigator
import se.gustavkarlsson.skylight.android.lib.navigation.screens
import se.gustavkarlsson.skylight.android.lib.permissions.Permission
import se.gustavkarlsson.skylight.android.lib.permissions.PermissionsComponent
import se.gustavkarlsson.skylight.android.lib.scopedservice.ServiceId
import se.gustavkarlsson.skylight.android.lib.scopedservice.ServiceTag
import se.gustavkarlsson.skylight.android.lib.ui.compose.ScreenBackground
import se.gustavkarlsson.skylight.android.lib.ui.compose.SearchFieldState
import se.gustavkarlsson.skylight.android.lib.ui.compose.ToggleButtonState
import se.gustavkarlsson.skylight.android.lib.ui.compose.collectAsLifecycleAwareState
import se.gustavkarlsson.skylight.android.lib.ui.getOrRegisterService
import se.gustavkarlsson.skylight.android.lib.ui.getService
import se.gustavkarlsson.skylight.android.lib.ui.startStopScope

private val VIEW_MODEL_ID = ServiceId("mainViewModel")

@Parcelize
object MainScreen : Screen {
    override val name get() = ScreenName.Main

    override val scopeStart get() = "main"

    private val AppCompatActivity.optionalViewModel: MainViewModel?
        get() = getService(VIEW_MODEL_ID)

    override fun AppCompatActivity.onStartStopScope(scope: CoroutineScope) {
        optionalViewModel?.onEvent(Event.RefreshLocationPermission)
    }

    override fun AppCompatActivity.onBackPress(): BackPress {
        return when ((optionalViewModel?.state?.value as? ViewState.Ready)?.appBar) {
            null, is AppBarState.PlaceSelected -> BackPress.NOT_HANDLED
            is AppBarState.Searching -> {
                optionalViewModel?.onEvent(Event.SearchChanged(SearchFieldState.Inactive))
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
    override fun AppCompatActivity.Content(tag: ServiceTag, scope: CoroutineScope) {
        val viewModel = getOrRegisterService(VIEW_MODEL_ID, tag) {
            MainViewModelComponent.build().viewModel()
        }
        val state by viewModel.state.collectAsLifecycleAwareState()
        Content(
            state = state,
            onBannerActionClicked = { event ->
                when (event) {
                    BannerData.Event.RequestBackgroundLocationPermission ->
                        requestPermission(Permission.BackgroundLocation)
                    BannerData.Event.OpenAppDetails ->
                        openAppDetails()
                }
            },
            onClickGrantLocationPermission = { requestPermission(Permission.Location) },
            onClickOpenSettings = { openAppDetails() },
            onClickTurnOffNotifications = { viewModel.onEvent(Event.TurnOffCurrentLocationNotifications) },
            onAboutClicked = { navigator.goTo(screens.about) },
            onEvent = { event -> viewModel.onEvent(event) },
        )
    }
}

@Composable
@Preview
private fun PreviewContent() {
    Content(
        state = ViewState.Ready(
            appBar = AppBarState.PlaceSelected(TextRef.string("Some Place")),
            content = ContentState.PlaceSelected(
                chanceLevelText = TextRef.EMPTY,
                errorBannerData = null,
                notificationsButtonState = ToggleButtonState.Enabled(checked = false),
                bookmarkButtonState = ToggleButtonState.Enabled(checked = true),
                factorItems = emptyList(),
                onBookmarkClickedEvent = Event.Noop,
                notificationLevelItems = emptyList(),
            )
        ),
        onBannerActionClicked = {},
        onClickGrantLocationPermission = {},
        onClickOpenSettings = {},
        onClickTurnOffNotifications = {},
        onAboutClicked = {},
        onEvent = {},
    )
}

@Composable
private fun Content(
    state: ViewState,
    onBannerActionClicked: (BannerData.Event) -> Unit,
    onClickGrantLocationPermission: () -> Unit,
    onClickOpenSettings: () -> Unit,
    onClickTurnOffNotifications: () -> Unit,
    onAboutClicked: () -> Unit,
    onEvent: (Event) -> Unit,
) {
    ScreenBackground {
        @Exhaustive
        when (state) {
            ViewState.Loading -> Unit
            is ViewState.Ready -> {
                Ready(
                    state = state,
                    onBannerActionClicked = onBannerActionClicked,
                    onAboutClicked = onAboutClicked,
                    onEvent = onEvent,
                    onClickGrantLocationPermission = onClickGrantLocationPermission,
                    onClickOpenSettings = onClickOpenSettings,
                )
            }
            ViewState.RequiresBackgroundLocationPermission -> {
                RequiresBackgroundLocationPermission(
                    onClickOpenSettings = onClickOpenSettings,
                    onClickTurnOffNotifications = onClickTurnOffNotifications,
                )
            }
        }
    }
}
