package se.gustavkarlsson.skylight.android.feature.main.view

import android.annotation.SuppressLint
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
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
    // FIXME crossfade?
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
                    Column {
                        // FIXME build layout and fix string resources
                        Text(stringResource(R.string.location_permission_denied_message))
                        Text("Open the permission dialog to allow the permission")
                        Button(onClick = onClickGrantLocationPermission) {
                            Text(stringResource(R.string.allow))
                        }
                        Text("You can also search for a location by name")
                        Button(
                            onClick = {
                                // FIXME duplicated
                                // FIXME doesn't focus search
                                onEvent(Event.SearchChanged(SearchFieldState.Active("")))
                            },
                        ) {
                            Text(stringResource(R.string.place_search))
                        }
                    }
                }
                is ContentState.RequiresLocationPermission.UseAppSettings -> {
                    Column {
                        // FIXME build layout and fix string resources
                        Text(stringResource(R.string.location_permission_denied_forever_message))
                        Text("It seems like you have previously denied the permission. To resolve this, open the settings screen and allow the background permission")
                        Button(onClick = onClickOpenSettings) {
                            Text(stringResource(R.string.open_settings))
                        }
                        Text("You can also search for a location by name")
                        Button(
                            onClick = {
                                // FIXME duplicated
                                // FIXME doesn't focus search
                                onEvent(Event.SearchChanged(SearchFieldState.Active("")))
                            },
                        ) {
                            Text(stringResource(R.string.place_search))
                        }
                    }
                }
            }
        }
    }
}
