package se.gustavkarlsson.skylight.android.feature.main.view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.AlertDialog
import androidx.compose.material.AppBarDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.ListItem
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlin.time.Instant
import se.gustavkarlsson.skylight.android.feature.main.viewmodel.ContentState
import se.gustavkarlsson.skylight.android.feature.main.viewmodel.Event
import se.gustavkarlsson.skylight.android.feature.main.viewmodel.SearchResult
import se.gustavkarlsson.skylight.android.lib.location.Location
import se.gustavkarlsson.skylight.android.lib.places.Place
import se.gustavkarlsson.skylight.android.lib.places.PlaceId
import se.gustavkarlsson.skylight.android.lib.ui.compose.Colors
import se.gustavkarlsson.skylight.android.lib.ui.compose.textRef

@Preview
@Composable
private fun PreviewSearchResults() {
    SearchResults(
        modifier = Modifier,
        state = ContentState.Searching.Ok(
            searchResults = listOf(
                SearchResult.Known.Current(
                    name = "Umeå",
                    selected = true,
                    notifications = false,
                ),
                SearchResult.Known.Saved(
                    place = Place.Saved(
                        id = PlaceId.Saved(2),
                        name = "Recent",
                        location = Location(3.0, 4.0),
                        lastChanged = Instant.fromEpochMilliseconds(0),
                    ),
                    selected = false,
                    notifications = false,
                ),
                SearchResult.New(
                    name = "A place I searched",
                    details = "Somewhere street",
                    location = Location(5.0, 6.0),
                ),
            ),
            dialog = null,
        ),
        onEvent = {},
    )
}

@Composable
internal fun SearchResults(
    modifier: Modifier = Modifier,
    state: ContentState.Searching,
    onEvent: (Event) -> Unit,
) {
    Surface(
        modifier = modifier,
        elevation = AppBarDefaults.TopAppBarElevation / 2,
        color = Colors.primarySurface,
    ) {
        when (state) {
            is ContentState.Searching.Error -> {
                Box(
                    modifier = Modifier
                        .padding(
                            WindowInsets.navigationBars
                                .union(WindowInsets.ime)
                                .asPaddingValues(),
                        )
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
                    contentPadding = WindowInsets.navigationBars.union(WindowInsets.ime).asPaddingValues(),
                ) {
                    items(state.searchResults) { item ->
                        ListItem(item = item, onEvent = onEvent)
                    }
                }
                state.dialog?.let { dialogData ->
                    AlertDialog(
                        onDismissRequest = { onEvent(dialogData.dismissEvent) },
                        confirmButton = {
                            val confirmData = dialogData.confirmData
                            TextButton(
                                onClick = { onEvent(confirmData.event) },
                            ) {
                                Text(text = textRef(confirmData.text))
                            }
                        },
                        dismissButton = {
                            val cancelData = dialogData.cancelData
                            TextButton(
                                onClick = { onEvent(cancelData.event) },
                            ) {
                                Text(text = textRef(cancelData.text))
                            }
                        },
                        text = {
                            Text(text = textRef(dialogData.text))
                        },
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
private fun ListItem(
    item: SearchResult,
    onEvent: (Event) -> Unit,
) {
    val itemModifier = if (item.selected) {
        Modifier.background(Colors.onSurface.copy(alpha = 0.1f))
    } else {
        Modifier
    }
    ListItem(
        modifier = itemModifier.combinedClickable(
            onLongClick = item.longClickEvent?.let { { onEvent(it) } },
            onClick = { onEvent(item.clickEvent) },
        ),
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
        },
    ) {
        Text(
            text = textRef(item.title),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
