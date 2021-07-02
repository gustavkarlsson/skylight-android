package se.gustavkarlsson.skylight.android.feature.main.view

import android.annotation.SuppressLint
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.navigationBarsPadding
import se.gustavkarlsson.skylight.android.feature.main.R
import se.gustavkarlsson.skylight.android.feature.main.viewmodel.BannerData
import se.gustavkarlsson.skylight.android.feature.main.viewmodel.ContentState
import se.gustavkarlsson.skylight.android.feature.main.viewmodel.Event
import se.gustavkarlsson.skylight.android.feature.main.viewmodel.ViewState
import se.gustavkarlsson.skylight.android.lib.ui.compose.Colors
import se.gustavkarlsson.skylight.android.lib.ui.compose.SearchFieldState
import se.gustavkarlsson.skylight.android.lib.ui.compose.Typography

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
                    Column(modifier = Modifier.padding(16.dp)) {
                        // FIXME lots of duplication here
                        Text(
                            text = stringResource(R.string.location_permission_required),
                            style = Typography.h5,
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(stringResource(R.string.location_permission_denied_message))
                        Spacer(modifier = Modifier.height(16.dp))
                        ExtendedFloatingActionButton(
                            backgroundColor = Colors.primary,
                            text = { Text(stringResource(R.string.location_permission)) },
                            onClick = onClickGrantLocationPermission,
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        TextButton(
                            onClick = { onEvent(Event.SearchChanged(SearchFieldState.Active(""))) },
                        ) {
                            Text(stringResource(R.string.location_permission_select_other))
                        }
                    }
                }
                is ContentState.RequiresLocationPermission.UseAppSettings -> {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // FIXME lots of duplication here
                        Text(
                            text = stringResource(R.string.location_permission_denied_forever_title),
                            style = Typography.h5,
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(stringResource(R.string.location_permission_denied_forever_message))
                        Spacer(modifier = Modifier.height(16.dp))
                        ExtendedFloatingActionButton(
                            backgroundColor = Colors.primary,
                            text = { Text(stringResource(R.string.open_settings)) },
                            onClick = onClickOpenSettings,
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        TextButton(
                            onClick = { onEvent(Event.SearchChanged(SearchFieldState.Active(""))) },
                        ) {
                            Text(stringResource(R.string.location_permission_select_other))
                        }
                    }
                }
            }
        }
    }
}
