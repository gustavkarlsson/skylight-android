package se.gustavkarlsson.skylight.android.feature.main.view

import android.app.Activity
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import se.gustavkarlsson.skylight.android.feature.main.R
import se.gustavkarlsson.skylight.android.feature.main.viewmodel.ContentState
import se.gustavkarlsson.skylight.android.feature.main.viewmodel.Event
import se.gustavkarlsson.skylight.android.feature.main.viewmodel.ViewState
import se.gustavkarlsson.skylight.android.lib.ui.compose.Colors
import se.gustavkarlsson.skylight.android.lib.ui.compose.Icons
import se.gustavkarlsson.skylight.android.lib.ui.compose.LargeDialog
import se.gustavkarlsson.skylight.android.lib.ui.compose.SearchFieldState
import se.gustavkarlsson.skylight.android.core.R as CoreR

@Composable
internal fun Ready(
    state: ViewState.Ready,
    onSettingsClicked: () -> Unit,
    onEvent: (Event) -> Unit,
    onClickGrantLocationPermission: () -> Unit,
    onClickOpenSettings: () -> Unit,
) {
    val pagerState = rememberPagerState()
    Scaffold(
        topBar = {
            TopAppBar(
                state = state.appBar,
                pagerState = pagerState,
                onSettingsClicked = onSettingsClicked,
                onEvent = onEvent,
            )
        },
    ) { paddingValues ->
        when (val content = state.content) {
            is ContentState.PlaceSelected -> {
                HorizontalPager(
                    count = pagerState.pageCount, // FIXME get from content instead?
                    state = pagerState,
                ) { page ->
                    // FIXME show different based on tab
                    SelectedPlace(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(WindowInsets.navigationBars.asPaddingValues())
                            .padding(paddingValues),
                        state = content,
                        onEvent = onEvent,
                    )
                }
            }
            is ContentState.Searching -> {
                SearchResults(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    state = content,
                    onEvent = onEvent,
                )
            }
            is ContentState.RequiresLocationPermission.UseDialog -> {
                LargeDialog(
                    modifier = Modifier
                        .dialogModifiers()
                        .padding(paddingValues),
                    image = { LocationIcon() },
                    title = stringResource(R.string.location_permission_title),
                    description = stringResource(R.string.location_permission_message),
                    primaryActionText = stringResource(R.string.grant_permission),
                    onClickPrimaryAction = onClickGrantLocationPermission,
                    secondaryActionText = stringResource(R.string.location_permission_select_manually),
                    onClickSecondaryAction = { onEvent(Event.SearchChanged(SearchFieldState.Active(""))) },
                )
            }
            is ContentState.RequiresLocationPermission.UseAppSettings -> {
                LargeDialog(
                    modifier = Modifier
                        .dialogModifiers()
                        .padding(paddingValues),
                    image = { LocationIcon() },
                    title = stringResource(R.string.location_permission_denied_forever_title),
                    description = stringResource(R.string.location_permission_denied_forever_message),
                    primaryActionText = stringResource(R.string.open_settings),
                    onClickPrimaryAction = onClickOpenSettings,
                    secondaryActionText = stringResource(R.string.location_permission_select_manually),
                    onClickSecondaryAction = { onEvent(Event.SearchChanged(SearchFieldState.Active(""))) },
                )
            }
            is ContentState.RequiresLocationService -> {
                val activity = LocalContext.current as Activity
                LargeDialog(
                    modifier = Modifier
                        .dialogModifiers()
                        .padding(paddingValues),
                    image = { LocationIcon() },
                    title = stringResource(R.string.location_service_disabled_title),
                    description = stringResource(R.string.location_service_disabled_message),
                    primaryActionText = stringResource(CoreR.string.enable),
                    onClickPrimaryAction = { onEvent(Event.ResolveLocationSettings(activity)) },
                    secondaryActionText = stringResource(R.string.location_permission_select_manually),
                    onClickSecondaryAction = { onEvent(Event.SearchChanged(SearchFieldState.Active(""))) },
                )
            }
        }
    }
}

private fun Modifier.dialogModifiers(): Modifier {
    return this
        .fillMaxSize()
        .composed {
            padding(WindowInsets.navigationBars.asPaddingValues())
        }
        .padding(vertical = 16.dp, horizontal = 32.dp)
}

@Composable
private fun LocationIcon() {
    Icon(
        modifier = Modifier.fillMaxSize(0.3f),
        imageVector = Icons.LocationOn,
        contentDescription = null,
        tint = Colors.onBackground,
    )
}
