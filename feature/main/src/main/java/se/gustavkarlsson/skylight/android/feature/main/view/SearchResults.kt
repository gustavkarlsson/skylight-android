package se.gustavkarlsson.skylight.android.feature.main.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.AppBarDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.ListItem
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.cash.exhaustive.Exhaustive
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.navigationBarsWithImePadding
import com.google.accompanist.insets.rememberInsetsPaddingValues
import org.threeten.bp.Instant
import se.gustavkarlsson.skylight.android.feature.main.viewmodel.ContentState
import se.gustavkarlsson.skylight.android.feature.main.viewmodel.Event
import se.gustavkarlsson.skylight.android.feature.main.viewmodel.SearchResult
import se.gustavkarlsson.skylight.android.lib.location.Location
import se.gustavkarlsson.skylight.android.lib.places.Place
import se.gustavkarlsson.skylight.android.lib.places.PlaceId
import se.gustavkarlsson.skylight.android.lib.ui.compose.Colors
import se.gustavkarlsson.skylight.android.lib.ui.compose.navigationBarsWithIme
import se.gustavkarlsson.skylight.android.lib.ui.compose.textRef

@Preview
@Composable
private fun PreviewSearchResults() {
    SearchResults(
        modifier = Modifier,
        state = ContentState.Searching.Ok(
            listOf(
                SearchResult.Known.Current(
                    name = "Umeå",
                    selected = true,
                    notifications = false,
                ),
                SearchResult.Known.Saved(
                    place = Place.Saved(
                        id = PlaceId.Saved(1),
                        name = "Bookmarked",
                        location = Location(1.0, 2.0),
                        bookmarked = true,
                        lastChanged = Instant.EPOCH,
                    ),
                    selected = false,
                    notifications = true,
                ),
                SearchResult.Known.Saved(
                    place = Place.Saved(
                        id = PlaceId.Saved(2),
                        name = "Recent",
                        location = Location(3.0, 4.0),
                        bookmarked = false,
                        lastChanged = Instant.EPOCH,
                    ),
                    selected = false,
                    notifications = false,
                ),
                SearchResult.New(
                    name = "A place I searched",
                    details = "Somewhere street",
                    location = Location(5.0, 6.0),
                ),
            )
        ),
        onEvent = {},
    )
}

@Composable
internal fun SearchResults(
    modifier: Modifier = Modifier,
    state: ContentState.Searching,
    onEvent: (Event) -> Unit
) {
    Surface(
        modifier = modifier,
        elevation = AppBarDefaults.TopAppBarElevation / 2,
        color = Colors.primarySurface,
    ) {
        @Exhaustive
        when (state) {
            is ContentState.Searching.Error -> {
                Box(
                    modifier = Modifier
                        .navigationBarsWithImePadding()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        textAlign = TextAlign.Center,
                        text = textRef(state.text),
                        color = Colors.error,
                    )
                }
            }
            is ContentState.Searching.Ok -> {
                LazyColumn(
                    contentPadding = rememberInsetsPaddingValues(LocalWindowInsets.current.navigationBarsWithIme),
                ) {
                    items(state.searchResults) { item ->
                        ListItem(item = item, onEvent = onEvent)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
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
            Icon(
                imageVector = item.icon,
                contentDescription = null,
            )
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
        trailing = item.trailingIcon?.let { icon ->
            {
                Icon(
                    imageVector = icon,
                    tint = Colors.onSurfaceWeaker,
                    contentDescription = null,
                )
            }
        }
    ) {
        Text(
            text = textRef(item.title),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
