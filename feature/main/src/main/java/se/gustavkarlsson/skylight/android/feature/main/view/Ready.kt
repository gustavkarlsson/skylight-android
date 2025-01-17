package se.gustavkarlsson.skylight.android.feature.main.view

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import se.gustavkarlsson.skylight.android.feature.main.R
import se.gustavkarlsson.skylight.android.feature.main.view.linechart.AutomaticGridFactory
import se.gustavkarlsson.skylight.android.feature.main.view.linechart.DefaultGridLine
import se.gustavkarlsson.skylight.android.feature.main.view.linechart.EmptyLabelFactory
import se.gustavkarlsson.skylight.android.feature.main.view.linechart.GridLine
import se.gustavkarlsson.skylight.android.feature.main.view.linechart.Label
import se.gustavkarlsson.skylight.android.feature.main.view.linechart.Line
import se.gustavkarlsson.skylight.android.feature.main.view.linechart.LineChart
import se.gustavkarlsson.skylight.android.feature.main.view.linechart.Point
import se.gustavkarlsson.skylight.android.feature.main.view.linechart.StraightLineDrawer
import se.gustavkarlsson.skylight.android.feature.main.viewmodel.ContentState
import se.gustavkarlsson.skylight.android.feature.main.viewmodel.Event
import se.gustavkarlsson.skylight.android.feature.main.viewmodel.PlaceData
import se.gustavkarlsson.skylight.android.feature.main.viewmodel.ViewState
import se.gustavkarlsson.skylight.android.lib.ui.compose.Colors
import se.gustavkarlsson.skylight.android.lib.ui.compose.Icons
import se.gustavkarlsson.skylight.android.lib.ui.compose.LargeDialog
import se.gustavkarlsson.skylight.android.lib.ui.compose.SearchFieldState
import java.time.format.TextStyle
import java.util.Locale
import kotlin.math.min
import kotlin.time.Duration.Companion.hours
import se.gustavkarlsson.skylight.android.core.R as CoreR

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun Ready(
    state: ViewState.Ready,
    onSettingsClicked: () -> Unit,
    onEvent: (Event) -> Unit,
    onClickGrantLocationPermission: () -> Unit,
    onClickOpenSettings: () -> Unit,
) {
    val pagerState = rememberPagerState {
        (state.content as? ContentState.PlaceVisible)?.placeData?.size ?: 0
    }
    Scaffold(
        topBar = {
            TopAppBar(
                state = state.appBar,
                pagerState = pagerState,
                onSettingsClicked = onSettingsClicked,
                onEvent = onEvent,
            )
        },
    ) { paddingValues ->
        when (val content = state.content) {
            is ContentState.PlaceVisible -> {
                HorizontalPager(
                    state = pagerState,
                ) { page ->
                    when (val data = content.placeData[page]) {
                        is PlaceData.Current -> {
                            Current(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(WindowInsets.navigationBars.asPaddingValues())
                                    .padding(paddingValues),
                                state = data,
                                onEvent = onEvent,
                            )
                        }

                        is PlaceData.Forecast -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(WindowInsets.navigationBars.asPaddingValues())
                                    .padding(paddingValues),
                                contentAlignment = Alignment.Center,
                            ) {
                                val points = data.chancesByTime.map { (timestamp, chance) ->
                                    Point(timestamp.epochSeconds.toDouble(), chance.value ?: 0.0)
                                }
                                val lines = listOf(Line(points, StraightLineDrawer(Color.Cyan)))
                                LineChart(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(1.5f),
                                    lines = lines,
                                    viewportFactory = { valueRange ->
                                        // X is epoch seconds
                                        // y is chance
                                        valueRange.copy(
                                            // FIXME don't get current time from here
                                            minX = min(valueRange.minX, Clock.System.now().epochSeconds.toDouble()),
                                            minY = 0.0,
                                            maxY = 1.0,
                                        )
                                    },
                                    xLabelFactory = { epochSecondsRange ->
                                        // FIXME don't get TZ from here
                                        val timeZone = TimeZone.currentSystemDefault()
                                        val startTime = Instant.fromEpochSeconds(epochSecondsRange.start.toLong())
                                            .toLocalDateTime(timeZone)
                                        val endTime = Instant.fromEpochSeconds(epochSecondsRange.endInclusive.toLong())
                                            .toLocalDateTime(timeZone)
                                        buildList {
                                            var date = startTime.date
                                            while (date in startTime.date..endTime.date) {
                                                val label = Label(
                                                    value = date.atStartOfDayIn(timeZone).epochSeconds.toDouble(),
                                                    text = date.dayOfWeek.getDisplayName(
                                                        TextStyle.FULL,
                                                        Locale.getDefault(), // FIXME don't get locale from here
                                                    ),
                                                    color = Color.White,
                                                )
                                                add(label)
                                                date = date.plus(DatePeriod(days = 1))
                                            }
                                        }
                                    },
                                    yLabelFactory = EmptyLabelFactory,
                                    horizontalGridFactory = {
                                        val drawer = DefaultGridLine.copy(color = Color.Gray)
                                        listOf(
                                            GridLine(0.33 / 2, drawer),
                                            GridLine(0.5, drawer),
                                            GridLine(0.66 + (0.33 / 2), drawer),
                                        )
                                    },
                                    verticalGridFactory = AutomaticGridFactory(
                                        targetCount = 8.0,
                                        baseInterval = 1.hours.inWholeSeconds.toDouble(),
                                        drawer = DefaultGridLine.copy(color = Color.Gray),
                                    ),
                                    contentPadding = PaddingValues(bottom = 48.dp),
                                )
                            }
                        }
                    }
                }
            }

            is ContentState.Searching -> {
                SearchResults(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    state = content,
                    onEvent = onEvent,
                )
            }

            is ContentState.RequiresLocationPermission.UseDialog -> {
                LargeDialog(
                    modifier = Modifier
                        .dialogModifiers()
                        .padding(paddingValues),
                    image = { LocationIcon() },
                    title = stringResource(R.string.location_permission_title),
                    description = stringResource(R.string.location_permission_message),
                    primaryActionText = stringResource(R.string.grant_permission),
                    onClickPrimaryAction = onClickGrantLocationPermission,
                    secondaryActionText = stringResource(R.string.location_permission_select_manually),
                    onClickSecondaryAction = { onEvent(Event.SearchChanged(SearchFieldState.Active(""))) },
                )
            }

            is ContentState.RequiresLocationPermission.UseAppSettings -> {
                LargeDialog(
                    modifier = Modifier
                        .dialogModifiers()
                        .padding(paddingValues),
                    image = { LocationIcon() },
                    title = stringResource(R.string.location_permission_denied_forever_title),
                    description = stringResource(R.string.location_permission_denied_forever_message),
                    primaryActionText = stringResource(R.string.open_settings),
                    onClickPrimaryAction = onClickOpenSettings,
                    secondaryActionText = stringResource(R.string.location_permission_select_manually),
                    onClickSecondaryAction = { onEvent(Event.SearchChanged(SearchFieldState.Active(""))) },
                )
            }

            is ContentState.RequiresLocationService -> {
                val activity = requireNotNull(LocalActivity.current)
                LargeDialog(
                    modifier = Modifier
                        .dialogModifiers()
                        .padding(paddingValues),
                    image = { LocationIcon() },
                    title = stringResource(R.string.location_service_disabled_title),
                    description = stringResource(R.string.location_service_disabled_message),
                    primaryActionText = stringResource(CoreR.string.enable),
                    onClickPrimaryAction = { onEvent(Event.ResolveLocationSettings(activity)) },
                    secondaryActionText = stringResource(R.string.location_permission_select_manually),
                    onClickSecondaryAction = { onEvent(Event.SearchChanged(SearchFieldState.Active(""))) },
                )
            }
        }
    }
}

private fun Modifier.dialogModifiers(): Modifier {
    return this
        .fillMaxSize()
        .composed {
            padding(WindowInsets.navigationBars.asPaddingValues())
        }
        .padding(vertical = 16.dp, horizontal = 32.dp)
}

@Composable
private fun LocationIcon() {
    Icon(
        modifier = Modifier.fillMaxSize(0.3f),
        imageVector = Icons.LocationOn,
        contentDescription = null,
        tint = Colors.onBackground,
    )
}
