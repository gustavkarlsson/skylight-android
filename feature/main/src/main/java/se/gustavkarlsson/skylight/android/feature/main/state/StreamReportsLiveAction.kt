package se.gustavkarlsson.skylight.android.feature.main.state

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import org.threeten.bp.Duration
import se.gustavkarlsson.conveyor.Action
import se.gustavkarlsson.conveyor.AtomicStateFlow
import se.gustavkarlsson.skylight.android.core.utils.throttle
import se.gustavkarlsson.skylight.android.feature.main.viewmodel.StreamThrottle
import se.gustavkarlsson.skylight.android.lib.aurora.AuroraReportProvider
import se.gustavkarlsson.skylight.android.lib.aurora.LoadableAuroraReport
import se.gustavkarlsson.skylight.android.lib.location.Location
import se.gustavkarlsson.skylight.android.lib.places.Place
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
internal class StreamReportsLiveAction @Inject constructor(
    private val auroraReportProvider: AuroraReportProvider,
    @StreamThrottle private val throttleDuration: Duration,
) : Action<State> {
    override suspend fun execute(stateFlow: AtomicStateFlow<State>) {
        stateFlow
            .isLive()
            .flatMapLatest { live ->
                if (live) {
                    stateFlow.reports()
                } else emptyFlow()
            }
            .throttle(throttleDuration.toMillis())
            .collectLatest { report ->
                stateFlow.update(report)
            }
    }

    private fun AtomicStateFlow<State>.isLive(): Flow<Boolean> =
        storeSubscriberCount
            .map { count -> count > 0 }
            .distinctUntilChanged()

    private fun Flow<State>.reports(): Flow<LoadableAuroraReport> =
        selectedPlaceLocations()
            .flatMapLatest { location ->
                if (location == null) {
                    flowOf(LoadableAuroraReport.LOADING) // FIXME replace with "no location"
                } else flow {
                    emitAll(auroraReportProvider.stream(location))
                }
            }

    private fun Flow<State>.selectedPlaceLocations(): Flow<Location?> =
        map { state ->
            when (val selectedPlace = state.selectedPlace) {
                null -> null
                Place.Current -> state.currentLocation.orNull()?.orNull()
                is Place.Saved -> selectedPlace.location
            }
        }.distinctUntilChanged()

    private suspend fun AtomicStateFlow<State>.update(report: LoadableAuroraReport) {
        update {
            when (this) {
                is State.Loading -> copy(selectedAuroraReport = report)
                is State.Ready -> copy(selectedAuroraReport = report)
            }
        }
    }
}

private data class UpdateData(val place: Place, val report: LoadableAuroraReport)
