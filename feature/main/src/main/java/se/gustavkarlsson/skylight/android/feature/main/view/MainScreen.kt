package se.gustavkarlsson.skylight.android.feature.main.view

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenCreated
import com.ioki.textref.TextRef
import kotlinx.coroutines.launch
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

private val VIEW_MODEL_ID = ServiceId("mainViewModel")

@Parcelize
object MainScreen : Screen {
    override val type: Screen.Type get() = Screen.Type.Main

    override val scopeStart get() = "main"

    private val optionalViewModel get() = getService<MainViewModel>(VIEW_MODEL_ID)

    override fun onBackPress(): BackPress {
        return when ((optionalViewModel?.state?.value as? ViewState.Ready)?.appBar) {
            null, is AppBarState.PlaceSelected -> BackPress.NOT_HANDLED
            is AppBarState.Searching -> {
                optionalViewModel?.onEvent(Event.SearchChanged(SearchFieldState.Inactive))
                BackPress.HANDLED
            }
        }
    }

    private fun openAppDetails(activity: Activity) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Uri.fromParts("package", activity.packageName, null)
        activity.startActivity(intent)
    }

    private fun requestPermission(activity: FragmentActivity, permission: Permission) {
        activity.lifecycleScope.launch {
            activity.lifecycle.whenCreated {
                PermissionsComponent.instance.permissionRequester()
                    .request(activity, permission)
            }
        }
    }

    @Composable
    override fun Content(activity: AppCompatActivity, tag: ServiceTag) {
        val viewModel = getOrRegisterService(VIEW_MODEL_ID, tag) {
            MainViewModelComponent.build().viewModel()
        }
        val state by viewModel.state.collectAsLifecycleAwareState()
        RefreshLocationPermission(activity)
        Content(
            state = state,
            onBannerActionClicked = { event ->
                when (event) {
                    BannerData.Event.RequestBackgroundLocationPermission ->
                        requestPermission(activity, Permission.BackgroundLocation)
                    BannerData.Event.OpenAppDetails ->
                        openAppDetails(activity)
                }
            },
            onClickGrantLocationPermission = {
                requestPermission(activity, Permission.Location)
            },
            onClickOpenSettings = { openAppDetails(activity) },
            onClickTurnOffNotifications = { viewModel.onEvent(Event.TurnOffCurrentLocationNotifications) },
            onSettingsClicked = { navigator.goTo(screens.settings) },
            onEvent = { event -> viewModel.onEvent(event) },
        )
    }

    @Composable
    private fun RefreshLocationPermission(activity: AppCompatActivity) {
        DisposableEffect(key1 = null) {
            val observer = object : DefaultLifecycleObserver {
                override fun onStart(owner: LifecycleOwner) {
                    optionalViewModel?.onEvent(Event.RefreshLocationPermission)
                }
            }
            activity.lifecycle.addObserver(observer)
            onDispose {
                activity.lifecycle.removeObserver(observer)
            }
        }
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
                onNotificationClickedEvent = Event.Noop,
            )
        ),
        onBannerActionClicked = {},
        onClickGrantLocationPermission = {},
        onClickOpenSettings = {},
        onClickTurnOffNotifications = {},
        onSettingsClicked = {},
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
    onSettingsClicked: () -> Unit,
    onEvent: (Event) -> Unit,
) {
    ScreenBackground {
        when (state) {
            ViewState.Loading -> Unit
            is ViewState.Ready -> {
                Ready(
                    state = state,
                    onBannerActionClicked = onBannerActionClicked,
                    onSettingsClicked = onSettingsClicked,
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
