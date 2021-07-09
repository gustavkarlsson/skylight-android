package se.gustavkarlsson.skylight.android.feature.main.view

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.AppBarDefaults
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideTextStyle
import androidx.compose.material.Surface
import androidx.compose.material.TopAppBar
import androidx.compose.material.contentColorFor
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.primarySurface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
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
        navigationIcon = {
            val active = state.toSearchFieldState() is SearchFieldState.Active
            IconButton(
                onClick = {
                    if (active) {
                        onEvent(Event.SearchChanged(SearchFieldState.Inactive))
                    } else {
                        onEvent(Event.SearchChanged(SearchFieldState.Active("")))
                    }
                }
            ) {
                Crossfade(targetState = active) { active ->
                    val imageVector = if (active) {
                        Icons.ArrowBack
                    } else Icons.Search
                    Icon(
                        imageVector = imageVector,
                        contentDescription = null,
                    )
                }
            }
        },
        title = {
            SearchField(
                modifier = Modifier.fillMaxSize(),
                state = state.toSearchFieldState(),
                inactiveText = textRef(textRef = (state as? AppBarState.PlaceSelected)?.title ?: TextRef.EMPTY),
                placeholderText = stringResource(id = R.string.place_search),
                textStyle = Typography.h6,
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

@Composable
private fun TopAppBar(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    navigationIcon: @Composable (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    backgroundColor: Color = MaterialTheme.colors.primarySurface,
    contentColor: Color = contentColorFor(backgroundColor),
    elevation: Dp = AppBarDefaults.TopAppBarElevation,
) {
    Surface(
        color = backgroundColor,
        elevation = elevation,
        modifier = modifier
    ) {
        val content: @Composable RowScope.() -> Unit = {
            if (navigationIcon == null) {
                Spacer(TitleInsetWithoutIcon)
            } else {
                Row(TitleIconModifier, verticalAlignment = Alignment.CenterVertically) {
                    CompositionLocalProvider(
                        LocalContentAlpha provides ContentAlpha.high,
                        content = navigationIcon
                    )
                }
            }

            Row(
                Modifier
                    .fillMaxHeight()
                    .weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ProvideTextStyle(value = MaterialTheme.typography.h6) {
                    CompositionLocalProvider(
                        LocalContentAlpha provides ContentAlpha.high,
                        content = title
                    )
                }
            }

            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Row(
                    Modifier.fillMaxHeight(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                    content = actions
                )
            }
        }
        TopAppBar(
            backgroundColor = Color.Transparent,
            contentColor = contentColor,
            elevation = 0.dp,
            modifier = Modifier.padding(contentPadding),
            content = content,
        )
    }
}

// These are stolen from androidx.compose.material.AppBarKt
private val AppBarHorizontalPadding = 4.dp

// Start inset for the title when there is no navigation icon provided
private val TitleInsetWithoutIcon = Modifier.width(16.dp - AppBarHorizontalPadding)

// Start inset for the title when there is a navigation icon provided
private val TitleIconModifier = Modifier
    .fillMaxHeight()
    .width(56.dp - AppBarHorizontalPadding)
