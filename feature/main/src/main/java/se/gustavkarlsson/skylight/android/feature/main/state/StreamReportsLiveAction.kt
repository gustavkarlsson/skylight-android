package se.gustavkarlsson.skylight.android.feature.main.state

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import org.threeten.bp.Duration
import se.gustavkarlsson.conveyor.Action
import se.gustavkarlsson.conveyor.UpdatableStateFlow
import se.gustavkarlsson.skylight.android.core.entities.Loadable
import se.gustavkarlsson.skylight.android.core.utils.throttle
import se.gustavkarlsson.skylight.android.feature.main.viewmodel.StreamThrottle
import se.gustavkarlsson.skylight.android.lib.aurora.AuroraReportProvider
import se.gustavkarlsson.skylight.android.lib.aurora.LoadableAuroraReport
import se.gustavkarlsson.skylight.android.lib.location.LocationProvider
import se.gustavkarlsson.skylight.android.lib.location.LocationResult
import se.gustavkarlsson.skylight.android.lib.places.Place
import javax.inject.Inject

internal class StreamReportsLiveAction @Inject constructor(
    private val locationProvider: LocationProvider,
    private val auroraReportProvider: AuroraReportProvider,
    @StreamThrottle private val throttleDuration: Duration,
) : Action<State> {
    override suspend fun execute(state: UpdatableStateFlow<State>) {
        isStoreLive(state).collectLatest { live ->
            if (live) {
                streamAndUpdateReports(state)
            }
        }
    }

    private fun isStoreLive(state: UpdatableStateFlow<State>): Flow<Boolean> =
        state.storeSubscriberCount
            .map { it > 0 }
            .distinctUntilChanged()

    private suspend fun streamAndUpdateReports(state: UpdatableStateFlow<State>) {
        selectedPlace(state).collectLatest { selectedPlace ->
            auroraReportProvider.stream(locationUpdates(selectedPlace))
                .throttle(throttleDuration.toMillis())
                .collectLatest { report ->
                    state.update(selectedPlace, report)
                }
        }
    }

    private fun selectedPlace(state: UpdatableStateFlow<State>): Flow<Place> =
        state
            .map { it.selectedPlace }
            .distinctUntilChangedBy { selected -> selected.id }

    private fun locationUpdates(selectedPlace: Place): Flow<Loadable<LocationResult>> {
        return when (selectedPlace) {
            Place.Current -> locationProvider.stream()
            is Place.Saved -> flowOf(Loadable.loaded(LocationResult.success(selectedPlace.location)))
        }
    }

    private suspend fun UpdatableStateFlow<State>.update(
        reportPlace: Place,
        report: LoadableAuroraReport
    ) {
        update {
            if (selectedPlace == reportPlace) {
                copy(selectedAuroraReport = report)
            } else {
                this
            }
        }
    }
}
