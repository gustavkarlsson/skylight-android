package se.gustavkarlsson.skylight.android.feature.main.view

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.accompanist.insets.ui.TopAppBar
import com.ioki.textref.TextRef
import se.gustavkarlsson.skylight.android.feature.main.R
import se.gustavkarlsson.skylight.android.feature.main.viewmodel.AppBarState
import se.gustavkarlsson.skylight.android.feature.main.viewmodel.Event
import se.gustavkarlsson.skylight.android.lib.ui.compose.Icons
import se.gustavkarlsson.skylight.android.lib.ui.compose.SearchField
import se.gustavkarlsson.skylight.android.lib.ui.compose.SearchFieldState
import se.gustavkarlsson.skylight.android.lib.ui.compose.Typography
import se.gustavkarlsson.skylight.android.lib.ui.compose.textRef

@Preview
@Composable
private fun PreviewTopAppBar() {
    TopAppBar(
        state = AppBarState.Searching("I'm searchi"),
        onAboutClicked = {},
        onEvent = {},
    )
}

@Composable
internal fun TopAppBar(
    state: AppBarState,
    onAboutClicked: () -> Unit,
    onEvent: (Event) -> Unit,
) {
    TopAppBar(
        contentPadding = rememberInsetsPaddingValues(LocalWindowInsets.current.statusBars),
        title = {
            // TODO doesn't fit unless text shrunk. Toolbar has fixed height. Lower case 'j' and 'g' get cut off.
            SearchField(
                modifier = Modifier.fillMaxSize(),
                state = state.toSearchFieldState(),
                inactiveText = textRef(textRef = (state as? AppBarState.PlaceSelected)?.title ?: TextRef.EMPTY),
                placeholderText = stringResource(id = R.string.place_search),
                textStyle = Typography.h6.copy(fontSize = Typography.h6.fontSize * 0.9),
                onStateChanged = { state -> onEvent(Event.SearchChanged(state)) },
            )
        },
        actions = {
            IconButton(onClick = onAboutClicked) {
                Icon(Icons.Info, contentDescription = stringResource(R.string.about))
            }
        }
    )
}

private fun AppBarState.toSearchFieldState(): SearchFieldState {
    return when (this) {
        is AppBarState.PlaceSelected -> SearchFieldState.Inactive
        is AppBarState.Searching -> SearchFieldState.Active(query)
    }
}
