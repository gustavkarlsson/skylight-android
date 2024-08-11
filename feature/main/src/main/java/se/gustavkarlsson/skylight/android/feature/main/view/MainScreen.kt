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
import com.ioki.textref.TextRef
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import se.gustavkarlsson.skylight.android.feature.main.state.TimeSpan
import se.gustavkarlsson.skylight.android.feature.main.viewmodel.AppBarState
import se.gustavkarlsson.skylight.android.feature.main.viewmodel.ContentState
import se.gustavkarlsson.skylight.android.feature.main.viewmodel.Event
import se.gustavkarlsson.skylight.android.feature.main.viewmodel.MainViewModel
import se.gustavkarlsson.skylight.android.feature.main.viewmodel.MainViewModelComponent
import se.gustavkarlsson.skylight.android.feature.main.viewmodel.TabItem
import se.gustavkarlsson.skylight.android.feature.main.viewmodel.ViewState
import se.gustavkarlsson.skylight.android.lib.navigation.Screen
import se.gustavkarlsson.skylight.android.lib.navigation.navigator
import se.gustavkarlsson.skylight.android.lib.navigation.screens
import se.gustavkarlsson.skylight.android.lib.permissions.Permission
import se.gustavkarlsson.skylight.android.lib.permissions.PermissionsComponent
import se.gustavkarlsson.skylight.android.lib.scopedservice.ServiceId
import se.gustavkarlsson.skylight.android.lib.scopedservice.ServiceTag
import se.gustavkarlsson.skylight.android.lib.ui.compose.ScreenBackground
import se.gustavkarlsson.skylight.android.lib.ui.compose.collectAsLifecycleAwareState
import se.gustavkarlsson.skylight.android.lib.ui.getOrRegisterService
import se.gustavkarlsson.skylight.android.lib.ui.getService

private val VIEW_MODEL_ID = ServiceId("mainViewModel")

@Parcelize
object MainScreen : Screen {
    override val type: Screen.Type get() = Screen.Type.Main

    override val scopeStart get() = "main"

    private val optionalViewModel get() = getService<MainViewModel>(VIEW_MODEL_ID)

    private fun openAppDetails(activity: Activity) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.setPackage(activity.packageName)
        intent.data = Uri.fromParts("package", activity.packageName, null)
        activity.startActivity(intent)
    }

    private fun requestPermission(activity: FragmentActivity, permission: Permission) {
        activity.lifecycleScope.launch {
            PermissionsComponent.instance.permissionRequester()
                .request(activity, permission)
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
            onClickGrantLocationPermission = {
                requestPermission(activity, Permission.Location)
            },
            onClickGrantBackgroundLocationPermission = {
                requestPermission(activity, Permission.BackgroundLocation)
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
            appBar = AppBarState.PlaceVisible(
                title = TextRef.string("Some Place"),
                tabs = listOf(
                    TabItem(
                        timeSpan = TimeSpan.Current,
                        text = TextRef.string("Now"),
                    ),
                    TabItem(
                        timeSpan = TimeSpan.Forecast,
                        text = TextRef.string("Forecast"),
                    ),
                ),
                tabsVisible = true,
            ),
            content = ContentState.PlaceVisible(
                placeData = emptyList(),
            ),
        ),
        onClickGrantLocationPermission = {},
        onClickGrantBackgroundLocationPermission = {},
        onClickOpenSettings = {},
        onClickTurnOffNotifications = {},
        onSettingsClicked = {},
        onEvent = {},
    )
}

@Composable
private fun Content(
    state: ViewState,
    onClickGrantLocationPermission: () -> Unit,
    onClickGrantBackgroundLocationPermission: () -> Unit,
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
                    onSettingsClicked = onSettingsClicked,
                    onEvent = onEvent,
                    onClickGrantLocationPermission = onClickGrantLocationPermission,
                    onClickOpenSettings = onClickOpenSettings,
                )
            }

            is ViewState.RequiresBackgroundLocationPermission.UseDialog -> {
                RequiresBackgroundLocationPermissionUseDialog(
                    description = state.description,
                    onClickGrantBackgroundLocationPermission = onClickGrantBackgroundLocationPermission,
                    onClickTurnOffNotifications = onClickTurnOffNotifications,
                )
            }

            is ViewState.RequiresBackgroundLocationPermission.UseAppSettings -> {
                RequiresBackgroundLocationPermissionUseAppSettings(
                    description = state.description,
                    onClickOpenSettings = onClickOpenSettings,
                    onClickTurnOffNotifications = onClickTurnOffNotifications,
                )
            }
        }
    }
}
