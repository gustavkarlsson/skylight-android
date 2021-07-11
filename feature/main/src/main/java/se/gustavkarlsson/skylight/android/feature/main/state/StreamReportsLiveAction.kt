package se.gustavkarlsson.skylight.android.feature.main.state

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import org.threeten.bp.Duration
import se.gustavkarlsson.conveyor.Action
import se.gustavkarlsson.conveyor.AtomicStateFlow
import se.gustavkarlsson.skylight.android.core.entities.Loadable
import se.gustavkarlsson.skylight.android.core.utils.throttle
import se.gustavkarlsson.skylight.android.feature.main.viewmodel.StreamThrottle
import se.gustavkarlsson.skylight.android.lib.aurora.AuroraReportProvider
import se.gustavkarlsson.skylight.android.lib.aurora.LoadableAuroraReport
import se.gustavkarlsson.skylight.android.lib.location.LocationResult
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
                    stateFlow.updates()
                } else emptyFlow()
            }
            .throttle(throttleDuration.toMillis())
            .collectLatest { updateData ->
                stateFlow.tryUpdate(updateData)
            }
    }

    private fun AtomicStateFlow<State>.isLive(): Flow<Boolean> =
        storeSubscriberCount
            .map { count -> count > 0 }
            .distinctUntilChanged()

    private fun Flow<State>.updates(): Flow<UpdateData> =
        selectedPlaces()
            .flatMapLatest { selectedPlace ->
                auroraReportProvider.stream(locations(selectedPlace))
                    .map { report ->
                        UpdateData(selectedPlace, report)
                    }
            }

    private fun Flow<State>.selectedPlaces(): Flow<Place> =
        mapNotNull { state -> state.selectedPlace }
            .distinctUntilChanged()

    private fun Flow<State>.locations(selectedPlace: Place): Flow<Loadable<LocationResult>> =
        when (selectedPlace) {
            Place.Current -> this.map { state -> state.currentLocation }
            is Place.Saved -> flowOf(Loadable.loaded(LocationResult.success(selectedPlace.location)))
        }

    private suspend fun AtomicStateFlow<State>.tryUpdate(updateData: UpdateData) {
        update {
            if (updateData.place == selectedPlace) {
                when (this) {
                    is State.Loading -> copy(selectedAuroraReport = updateData.report)
                    is State.Ready -> copy(selectedAuroraReport = updateData.report)
                }
            } else this
        }
    }
}

private data class UpdateData(val place: Place, val report: LoadableAuroraReport)
