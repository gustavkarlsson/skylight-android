package se.gustavkarlsson.skylight.android.feature.main.state

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import org.threeten.bp.Duration
import se.gustavkarlsson.conveyor.Action
import se.gustavkarlsson.conveyor.AtomicStateFlow
import se.gustavkarlsson.skylight.android.core.entities.Loadable
import se.gustavkarlsson.skylight.android.core.utils.throttle
import se.gustavkarlsson.skylight.android.feature.main.viewmodel.StreamThrottle
import se.gustavkarlsson.skylight.android.lib.aurora.AuroraReportProvider
import se.gustavkarlsson.skylight.android.lib.aurora.LoadableAuroraReport
import se.gustavkarlsson.skylight.android.lib.location.LocationProvider
import se.gustavkarlsson.skylight.android.lib.location.LocationResult
import se.gustavkarlsson.skylight.android.lib.permissions.Access
import se.gustavkarlsson.skylight.android.lib.permissions.Permission
import se.gustavkarlsson.skylight.android.lib.places.Place
import javax.inject.Inject

internal class StreamReportsLiveAction @Inject constructor(
    private val locationProvider: LocationProvider,
    private val auroraReportProvider: AuroraReportProvider,
    @StreamThrottle private val throttleDuration: Duration,
) : Action<State> {
    override suspend fun execute(stateFlow: AtomicStateFlow<State>) {
        stateFlow.isLive.collectLatest { live ->
            if (live) {
                streamAndUpdateReports(stateFlow)
            }
        }
    }

    private val AtomicStateFlow<State>.isLive: Flow<Boolean>
        get() = storeSubscriberCount
            .map { it > 0 }
            .distinctUntilChanged()

    private suspend fun streamAndUpdateReports(stateFlow: AtomicStateFlow<State>) {
        placeToStream(stateFlow).collectLatest { place ->
            if (place != null) {
                auroraReportProvider.stream(locationUpdates(place))
                    .throttle(throttleDuration.toMillis())
                    .collectLatest { report ->
                        stateFlow.update(place, report)
                    }
            }
        }
    }

    private fun placeToStream(stateFlow: AtomicStateFlow<State>): Flow<Place?> =
        stateFlow
            .map { state ->
                val currentSelected = state.selectedPlace == Place.Current
                val permissionGranted = state.permissions[Permission.Location] == Access.Granted
                if (currentSelected && !permissionGranted) {
                    null
                } else state.selectedPlace
            }
            .distinctUntilChangedBy { selected -> selected?.id }

    private fun locationUpdates(selectedPlace: Place): Flow<Loadable<LocationResult>> {
        return when (selectedPlace) {
            Place.Current -> locationProvider.stream()
            is Place.Saved -> flowOf(Loadable.loaded(LocationResult.success(selectedPlace.location)))
        }
    }

    private suspend fun AtomicStateFlow<State>.update(
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
