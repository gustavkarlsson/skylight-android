package se.gustavkarlsson.skylight.android.feature.main.view

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.ui.Scaffold
import se.gustavkarlsson.skylight.android.feature.main.R
import se.gustavkarlsson.skylight.android.feature.main.viewmodel.BannerData
import se.gustavkarlsson.skylight.android.feature.main.viewmodel.ContentState
import se.gustavkarlsson.skylight.android.feature.main.viewmodel.Event
import se.gustavkarlsson.skylight.android.feature.main.viewmodel.ViewState
import se.gustavkarlsson.skylight.android.lib.ui.compose.Colors
import se.gustavkarlsson.skylight.android.lib.ui.compose.Icons
import se.gustavkarlsson.skylight.android.lib.ui.compose.LargeDialog
import se.gustavkarlsson.skylight.android.lib.ui.compose.SearchFieldState

@Composable
internal fun Ready(
    state: ViewState.Ready,
    onBannerActionClicked: (BannerData.Event) -> Unit,
    onAboutClicked: () -> Unit,
    onEvent: (Event) -> Unit,
    onClickGrantLocationPermission: () -> Unit,
    onClickOpenSettings: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                state = state.appBar,
                onAboutClicked = onAboutClicked,
                onEvent = onEvent,
            )
        },
    ) { paddingValues ->
        val dummy = when (val content = state.content) {
            is ContentState.PlaceSelected -> {
                SelectedPlace(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .navigationBarsPadding(),
                    state = content,
                    onBannerActionClicked = onBannerActionClicked,
                    onEvent = onEvent,
                )
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
                    image = { MyLocationIcon() },
                    title = stringResource(R.string.location_permission_required),
                    description = stringResource(R.string.location_permission_denied_message),
                    primaryActionText = stringResource(R.string.location_permission),
                    onClickPrimaryAction = onClickGrantLocationPermission,
                    secondaryActionText = stringResource(R.string.location_permission_select_other),
                    onClickSecondaryAction = { onEvent(Event.SearchChanged(SearchFieldState.Active(""))) },
                )
            }
            is ContentState.RequiresLocationPermission.UseAppSettings -> {
                LargeDialog(
                    modifier = Modifier
                        .dialogModifiers()
                        .padding(paddingValues),
                    image = { MyLocationIcon() },
                    title = stringResource(R.string.location_permission_denied_forever_title),
                    description = stringResource(R.string.location_permission_denied_forever_message),
                    primaryActionText = stringResource(R.string.open_settings),
                    onClickPrimaryAction = onClickOpenSettings,
                    secondaryActionText = stringResource(R.string.location_permission_select_other),
                    onClickSecondaryAction = { onEvent(Event.SearchChanged(SearchFieldState.Active(""))) },
                )
            }
        }
    }
}

private fun Modifier.dialogModifiers(): Modifier {
    return this
        .fillMaxSize()
        .padding(vertical = 16.dp, horizontal = 32.dp)
        .navigationBarsPadding()
}

@Composable
private fun MyLocationIcon() {
    Icon(
        modifier = Modifier.fillMaxSize(0.3f),
        imageVector = Icons.MyLocation,
        contentDescription = null,
        tint = Colors.onBackground,
    )
}
