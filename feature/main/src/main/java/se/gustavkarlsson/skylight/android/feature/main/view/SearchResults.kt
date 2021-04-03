package se.gustavkarlsson.skylight.android.feature.main.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.ListItem
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.navigationBarsWithImePadding
import com.google.accompanist.insets.toPaddingValues
import se.gustavkarlsson.skylight.android.feature.main.viewmodel.Event
import se.gustavkarlsson.skylight.android.feature.main.viewmodel.SearchResult
import se.gustavkarlsson.skylight.android.feature.main.viewmodel.SearchViewState
import se.gustavkarlsson.skylight.android.feature.main.viewmodel.ViewState
import se.gustavkarlsson.skylight.android.lib.ui.compose.Colors
import se.gustavkarlsson.skylight.android.lib.ui.compose.navigationBarsWithIme
import se.gustavkarlsson.skylight.android.lib.ui.compose.textRef

@ExperimentalMaterialApi
@ExperimentalAnimationApi
@Composable
internal fun SearchResults(
    modifier: Modifier,
    state: ViewState,
    searchElevation: Dp,
    searchBackgroundColor: Color,
    onEvent: (Event) -> Unit
) {
    AnimatedVisibility(
        visible = state.search is SearchViewState.Open,
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        Surface(
            modifier = modifier,
            elevation = searchElevation,
            color = searchBackgroundColor,
        ) {
            when (state.search) {
                SearchViewState.Closed -> Unit
                is SearchViewState.Open.Error -> {
                    Box(
                        modifier = Modifier
                            .navigationBarsWithImePadding()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        // FIXME make this look better
                        Text(
                            textAlign = TextAlign.Center,
                            text = textRef(state.search.text),
                            color = Colors.error,
                        )
                    }
                }
                is SearchViewState.Open.Ok -> {
                    LazyColumn(
                        contentPadding = LocalWindowInsets.current.navigationBarsWithIme.toPaddingValues(),
                    ) {
                        items(state.search.searchResults) { item ->
                            ListItem(item = item, onEvent = onEvent)
                        }
                    }
                }
            }
            val progressVisible = (state.search as? SearchViewState.Open)?.inProgress == true
            if (progressVisible) {
                Text("---LOADING---") // FIXME replace with something better
            }
        }
    }
}

@ExperimentalMaterialApi
@Composable
private fun ListItem(
    item: SearchResult,
    onEvent: (Event) -> Unit
) {
    val itemModifier = if (item.selected) {
        Modifier.background(Colors.onSurface.copy(alpha = 0.1f))
    } else Modifier
    ListItem(
        modifier = itemModifier.clickable {
            onEvent(item.selectEvent)
        },
        icon = {
            Icon(item.icon, contentDescription = null)
        },
        secondaryText = item.subtitle?.let { subtitle ->
            {
                Text(
                    text = textRef(subtitle),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        },
    ) {
        Text(
            text = textRef(item.title),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
