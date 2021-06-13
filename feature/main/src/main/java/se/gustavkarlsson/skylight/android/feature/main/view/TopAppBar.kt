package se.gustavkarlsson.skylight.android.feature.main.view

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import se.gustavkarlsson.skylight.android.feature.main.R
import se.gustavkarlsson.skylight.android.feature.main.viewmodel.Event
import se.gustavkarlsson.skylight.android.feature.main.viewmodel.SearchViewState
import se.gustavkarlsson.skylight.android.lib.ui.compose.AppBarHorizontalPadding
import se.gustavkarlsson.skylight.android.lib.ui.compose.Icons
import se.gustavkarlsson.skylight.android.lib.ui.compose.SearchField
import se.gustavkarlsson.skylight.android.lib.ui.compose.SearchFieldState
import se.gustavkarlsson.skylight.android.lib.ui.compose.TopAppBar
import se.gustavkarlsson.skylight.android.lib.ui.compose.Typography

@Preview
@Composable
private fun PreviewTopAppBar() {
    TopAppBar(
        state = SearchViewState.Open.Ok("query", emptyList()),
        title = "Title",
        onAboutClicked = {},
        onEvent = {},
        backgroundColor = Color.White,
        elevation = 6.dp,
    )
}

@Composable
internal fun TopAppBar(
    state: SearchViewState,
    title: String,
    onAboutClicked: () -> Unit,
    onEvent: (Event) -> Unit,
    backgroundColor: Color,
    elevation: Dp,
) {
    TopAppBar(
        backgroundColor = backgroundColor,
        elevation = elevation,
        contentPadding = rememberInsetsPaddingValues(
            insets = LocalWindowInsets.current.statusBars,
            additionalStart = AppBarHorizontalPadding,
            additionalEnd = AppBarHorizontalPadding
        ),
        title = {
            // TODO doesn't fit unless text shrunk. Toolbar has fixed height. Lower case 'j' and 'g' get cut off.
            SearchField(
                modifier = Modifier.fillMaxSize(),
                state = state.toSearchFieldState(),
                inactiveText = title,
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

private fun SearchViewState.toSearchFieldState(): SearchFieldState {
    return when (this) {
        SearchViewState.Closed -> SearchFieldState.Inactive
        is SearchViewState.Open -> SearchFieldState.Active(query)
    }
}
