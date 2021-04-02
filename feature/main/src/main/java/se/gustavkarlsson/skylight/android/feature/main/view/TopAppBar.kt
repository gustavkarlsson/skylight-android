package se.gustavkarlsson.skylight.android.feature.main.view

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.toPaddingValues
import se.gustavkarlsson.skylight.android.feature.main.R
import se.gustavkarlsson.skylight.android.feature.main.viewmodel.Event
import se.gustavkarlsson.skylight.android.feature.main.viewmodel.SearchViewState
import se.gustavkarlsson.skylight.android.lib.ui.compose.AppBarHorizontalPadding
import se.gustavkarlsson.skylight.android.lib.ui.compose.Icons
import se.gustavkarlsson.skylight.android.lib.ui.compose.SearchField
import se.gustavkarlsson.skylight.android.lib.ui.compose.SearchFieldState
import se.gustavkarlsson.skylight.android.lib.ui.compose.TopAppBar

@Composable
internal fun TopAppBar(
    searchState: SearchViewState,
    title: String,
    onAboutClicked: () -> Unit,
    onEvent: (Event) -> Unit,
    backgroundColor: Color,
    elevation: Dp,
) {
    TopAppBar(
        backgroundColor = backgroundColor,
        elevation = elevation,
        contentPadding = LocalWindowInsets.current.statusBars
            .toPaddingValues(additionalHorizontal = AppBarHorizontalPadding),
        title = {
            SearchField(
                modifier = Modifier.fillMaxWidth(),
                state = searchState.toSearchFieldState(),
                inactiveText = title,
                placeholderText = stringResource(id = R.string.place_search),
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
