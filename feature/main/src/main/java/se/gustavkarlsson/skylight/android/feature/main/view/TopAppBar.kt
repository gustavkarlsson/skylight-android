package se.gustavkarlsson.skylight.android.feature.main.view

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
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
import com.ioki.textref.TextRef
import se.gustavkarlsson.skylight.android.feature.main.R
import se.gustavkarlsson.skylight.android.feature.main.viewmodel.AppBarState
import se.gustavkarlsson.skylight.android.feature.main.viewmodel.Event
import se.gustavkarlsson.skylight.android.lib.ui.compose.AutoMirroredIcons
import se.gustavkarlsson.skylight.android.lib.ui.compose.Icons
import se.gustavkarlsson.skylight.android.lib.ui.compose.SearchField
import se.gustavkarlsson.skylight.android.lib.ui.compose.SearchFieldState
import se.gustavkarlsson.skylight.android.lib.ui.compose.Typography
import se.gustavkarlsson.skylight.android.lib.ui.compose.textRef
import se.gustavkarlsson.skylight.android.core.R as CoreR

@Preview
@Composable
private fun PreviewTopAppBar() {
    TopAppBar(
        state = AppBarState.Searching("I'm searchi"),
        onSettingsClicked = {},
        onEvent = {},
    )
}

@Composable
internal fun TopAppBar(
    state: AppBarState,
    onSettingsClicked: () -> Unit,
    onEvent: (Event) -> Unit,
) {
    TopAppBar(
        contentPadding = WindowInsets.statusBars.asPaddingValues(),
        navigationIcon = {
            val active = state is AppBarState.Searching
            BackHandler(enabled = active) {
                onEvent(Event.SearchChanged(SearchFieldState.Inactive))
            }
            IconButton(
                onClick = {
                    if (active) {
                        onEvent(Event.SearchChanged(SearchFieldState.Inactive))
                    } else {
                        onEvent(Event.SearchChanged(SearchFieldState.Active("")))
                    }
                },
            ) {
                Crossfade(targetState = active) { active ->
                    val imageVector = if (active) {
                        AutoMirroredIcons.ArrowBack
                    } else {
                        Icons.Search
                    }
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
            IconButton(onClick = onSettingsClicked) {
                Icon(Icons.Settings, contentDescription = stringResource(CoreR.string.settings))
            }
        },
    )
}

private fun AppBarState.toSearchFieldState(): SearchFieldState {
    return when (this) {
        is AppBarState.PlaceSelected -> SearchFieldState.Inactive
        is AppBarState.Searching -> SearchFieldState.Active(query)
    }
}

// Everything is copied and modified from androidx.compose.material.TextFieldKt
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
        modifier = modifier,
    ) {
        val content: @Composable RowScope.() -> Unit = {
            if (navigationIcon == null) {
                Spacer(TitleInsetWithoutIcon)
            } else {
                Row(TitleIconModifier, verticalAlignment = Alignment.CenterVertically) {
                    CompositionLocalProvider(
                        LocalContentAlpha provides ContentAlpha.high,
                        content = navigationIcon,
                    )
                }
            }

            Row(
                Modifier
                    .fillMaxHeight()
                    .weight(1f),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ProvideTextStyle(value = MaterialTheme.typography.h6) {
                    CompositionLocalProvider(
                        LocalContentAlpha provides ContentAlpha.high,
                        content = title,
                    )
                }
            }

            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Row(
                    Modifier.fillMaxHeight(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                    content = actions,
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
