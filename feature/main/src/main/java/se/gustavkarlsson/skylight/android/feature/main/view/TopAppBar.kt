package se.gustavkarlsson.skylight.android.feature.main.view

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.material.AppBarDefaults
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LeadingIconTab
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideTextStyle
import androidx.compose.material.Surface
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.Text
import androidx.compose.material.contentColorFor
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarViewMonth
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Today
import androidx.compose.material.primarySurface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import com.ioki.textref.TextRef
import kotlinx.coroutines.launch
import se.gustavkarlsson.skylight.android.feature.main.R
import se.gustavkarlsson.skylight.android.feature.main.state.TimeSpan
import se.gustavkarlsson.skylight.android.feature.main.viewmodel.AppBarState
import se.gustavkarlsson.skylight.android.feature.main.viewmodel.Event
import se.gustavkarlsson.skylight.android.lib.ui.compose.AutoMirroredIcons
import se.gustavkarlsson.skylight.android.lib.ui.compose.Icons
import se.gustavkarlsson.skylight.android.lib.ui.compose.SearchField
import se.gustavkarlsson.skylight.android.lib.ui.compose.SearchFieldState
import se.gustavkarlsson.skylight.android.lib.ui.compose.Typography
import se.gustavkarlsson.skylight.android.lib.ui.compose.textRef
import se.gustavkarlsson.skylight.android.core.R as CoreR

@OptIn(ExperimentalPagerApi::class)
@Preview
@Composable
private fun PreviewTopAppBar() {
    TopAppBar(
        state = AppBarState.Searching("I'm searchi"),
        pagerState = rememberPagerState(),
        onSettingsClicked = {},
        onEvent = {},
    )
}

@OptIn(ExperimentalPagerApi::class)
@Composable
internal fun TopAppBar(
    state: AppBarState,
    pagerState: PagerState,
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
                modifier = Modifier
                    .height(IntrinsicSize.Max)
                    .fillMaxWidth(),
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
        bottomRow = (state as? AppBarState.PlaceSelected)?.let {
            {
                Tabs(state, pagerState)
            }
        },
    )
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun Tabs(
    state: AppBarState.PlaceSelected,
    pagerState: PagerState,
) {
    TabRow(
        selectedTabIndex = pagerState.currentPage,
        indicator = { tabPositions ->
            TabRowDefaults.Indicator(
                Modifier.pagerTabIndicatorOffset(pagerState, tabPositions),
            )
        },
        backgroundColor = Color.Transparent,
    ) {
        val scope = rememberCoroutineScope()
        state.tabs.forEachIndexed { index, tab ->
            LeadingIconTab(
                selected = pagerState.currentPage == index,
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                },
                text = {
                    Text(textRef(tab.text))
                },
                icon = {
                    val icon = when (tab.timeSpan) { // FIXME better icons
                        TimeSpan.Current -> Icons.Today
                        TimeSpan.Forecast -> Icons.CalendarViewMonth
                    }
                    Icon(icon, contentDescription = null)
                },
            )
        }
    }
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
    bottomRow: (@Composable () -> Unit)? = null,
    backgroundColor: Color = MaterialTheme.colors.primarySurface,
    contentColor: Color = contentColorFor(backgroundColor),
    elevation: Dp = AppBarDefaults.TopAppBarElevation,
) {
    Surface(
        color = backgroundColor,
        elevation = elevation,
        modifier = modifier,
    ) {
        val topRow: @Composable () -> Unit = {
            Row(Modifier.height(AppBarHeight)) {
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
        }
        TopAppBar(
            backgroundColor = Color.Transparent,
            contentColor = contentColor,
            elevation = 0.dp,
            modifier = modifier,
            contentPadding = contentPadding,
            content = {
                Column(Modifier.fillMaxWidth()) {
                    topRow()
                    bottomRow?.invoke()
                }
            },
        )
    }
}

// Stolen from androidx.compose.material.AppBarKt and tweaked to support more than a row
@Composable
fun TopAppBar(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colors.primarySurface,
    contentColor: Color = contentColorFor(backgroundColor),
    elevation: Dp = AppBarDefaults.TopAppBarElevation,
    contentPadding: PaddingValues = AppBarDefaults.ContentPadding,
    content: @Composable () -> Unit,
) {
    AppBar(
        backgroundColor,
        contentColor,
        elevation,
        contentPadding,
        RectangleShape,
        modifier = modifier,
        content = content,
    )
}

// Stolen from androidx.compose.material.AppBarKt and tweaked to support more than a row
@Composable
private fun AppBar(
    backgroundColor: Color,
    contentColor: Color,
    elevation: Dp,
    contentPadding: PaddingValues,
    shape: Shape,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Surface(
        color = backgroundColor,
        contentColor = contentColor,
        elevation = elevation,
        shape = shape,
        modifier = modifier
            .fillMaxWidth()
            .padding(contentPadding),
        content = content,
    )
}

// These are stolen from androidx.compose.material.AppBarKt
private val AppBarHeight = 56.dp
private val AppBarHorizontalPadding = 4.dp

// Start inset for the title when there is no navigation icon provided
private val TitleInsetWithoutIcon = Modifier.width(16.dp - AppBarHorizontalPadding)

// Start inset for the title when there is a navigation icon provided
private val TitleIconModifier = Modifier
    .fillMaxHeight()
    .width(56.dp - AppBarHorizontalPadding)
