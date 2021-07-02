package se.gustavkarlsson.skylight.android.feature.main.view

import android.annotation.SuppressLint
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.google.accompanist.insets.navigationBarsPadding
import se.gustavkarlsson.skylight.android.feature.main.R
import se.gustavkarlsson.skylight.android.feature.main.viewmodel.BannerData
import se.gustavkarlsson.skylight.android.feature.main.viewmodel.ContentState
import se.gustavkarlsson.skylight.android.feature.main.viewmodel.Event
import se.gustavkarlsson.skylight.android.feature.main.viewmodel.ViewState
import se.gustavkarlsson.skylight.android.lib.ui.compose.SearchFieldState

@SuppressLint("UnusedCrossfadeTargetStateParameter")
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
        Crossfade(
            modifier = Modifier.padding(paddingValues),
            targetState = state.content.javaClass,
        ) {
            val dummy = when (val content = state.content) {
                is ContentState.PlaceSelected -> {
                    SelectedPlace(
                        modifier = Modifier
                            .fillMaxSize()
                            .navigationBarsPadding(),
                        state = content,
                        onBannerActionClicked = onBannerActionClicked,
                        onEvent = onEvent,
                    )
                }
                is ContentState.Searching -> {
                    SearchResults(
                        modifier = Modifier.fillMaxSize(),
                        state = content,
                        onEvent = onEvent,
                    )
                }
                is ContentState.RequiresLocationPermission.UseDialog -> {
                    RequiresLocationPermission(
                        modifier = Modifier
                            .fillMaxSize()
                            .navigationBarsPadding(),
                        title = stringResource(R.string.location_permission_required),
                        description = stringResource(R.string.location_permission_denied_message),
                        primaryActionText = stringResource(R.string.location_permission),
                        onClickPrimaryAction = onClickGrantLocationPermission,
                        secondaryActionText = stringResource(R.string.location_permission_select_other),
                        onClickSecondaryAction = { onEvent(Event.SearchChanged(SearchFieldState.Active(""))) },
                    )
                }
                is ContentState.RequiresLocationPermission.UseAppSettings -> {
                    RequiresLocationPermission(
                        modifier = Modifier
                            .fillMaxSize()
                            .navigationBarsPadding(),
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
}
