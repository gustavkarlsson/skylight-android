package se.gustavkarlsson.skylight.android.feature.main.state

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import me.tatarka.inject.annotations.Inject
import se.gustavkarlsson.conveyor.Action
import se.gustavkarlsson.conveyor.AtomicStateFlow
import se.gustavkarlsson.skylight.android.core.entities.Loadable
import se.gustavkarlsson.skylight.android.core.entities.Loading
import se.gustavkarlsson.skylight.android.core.utils.throttleLatest
import se.gustavkarlsson.skylight.android.lib.aurora.AuroraForecastReport
import se.gustavkarlsson.skylight.android.lib.aurora.AuroraForecastReportProvider
import se.gustavkarlsson.skylight.android.lib.aurora.AuroraReportProvider
import se.gustavkarlsson.skylight.android.lib.aurora.LoadableAuroraReport
import se.gustavkarlsson.skylight.android.lib.location.Location
import se.gustavkarlsson.skylight.android.lib.places.Place
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalCoroutinesApi::class)
internal class StreamReportsLiveAction(
    private val auroraReportProvider: AuroraReportProvider,
    private val auroraForecastReportProvider: AuroraForecastReportProvider,
    private val throttleDuration: Duration,
    private val stayAlive: Duration,
) : Action<State> {

    @Inject
    constructor(
        auroraReportProvider: AuroraReportProvider,
        auroraForecastReportProvider: AuroraForecastReportProvider,
    ) : this(
        auroraReportProvider = auroraReportProvider,
        auroraForecastReportProvider = auroraForecastReportProvider,
        throttleDuration = 500.milliseconds,
        stayAlive = 1000.milliseconds,
    )

    override suspend fun execute(stateFlow: AtomicStateFlow<State>) {
        stateFlow
            .isLive()
            .flatMapLatest { live ->
                if (live) {
                    stateFlow.reports()
                } else {
                    emptyFlow()
                }
            }
            .throttleLatest(throttleDuration)
            .collectLatest { (report, forecast) ->
                stateFlow.update(report, forecast)
            }
    }

    private fun AtomicStateFlow<State>.isLive(): Flow<Boolean> =
        storeSubscriberCount
            .mapLatest { count ->
                val live = count > 0
                if (!live) {
                    delay(stayAlive)
                }
                live
            }
            .distinctUntilChanged()

    private fun Flow<State>.reports(): Flow<Pair<LoadableAuroraReport, Loadable<AuroraForecastReport>>> =
        selectedPlaceLocations()
            .flatMapLatest { location ->
                val reports = auroraReportProvider.stream(location)
                val forecasts = if (location != null) {
                    auroraForecastReportProvider.stream(location)
                } else {
                    flowOf(Loading)
                }
                combine(reports, forecasts) { a, b -> a to b }
            }

    private fun Flow<State>.selectedPlaceLocations(): Flow<Location?> =
        map { state ->
            when (val selectedPlace = state.selectedPlace) {
                null -> null
                Place.Current -> state.currentLocation.getOrNull()?.getOrNull() // TODO don't ignore location errors?
                is Place.Saved -> selectedPlace.location
            }
        }.distinctUntilChanged()

    private suspend fun AtomicStateFlow<State>.update(
        report: LoadableAuroraReport,
        forecast: Loadable<AuroraForecastReport>,
    ) {
        update {
            when (this) {
                is State.Loading -> copy(selectedAuroraReport = report, selectedAuroraForecastReport = forecast)
                is State.Ready -> copy(selectedAuroraReport = report, selectedAuroraForecastReport = forecast)
            }
        }
    }
}
