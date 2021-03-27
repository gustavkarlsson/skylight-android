package se.gustavkarlsson.skylight.android.feature.main

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.threeten.bp.Duration
import se.gustavkarlsson.conveyor.Action
import se.gustavkarlsson.conveyor.UpdatableStateFlow
import se.gustavkarlsson.skylight.android.core.entities.Loadable
import se.gustavkarlsson.skylight.android.lib.aurora.LoadableAuroraReport
import se.gustavkarlsson.skylight.android.lib.location.LocationResult
import se.gustavkarlsson.skylight.android.lib.places.Place

@ExperimentalCoroutinesApi
internal class StreamReportsLiveAction(
    private val currentLocation: Flow<Loadable<LocationResult>>,
    private val streamAuroraReports: (Flow<Loadable<LocationResult>>) -> Flow<LoadableAuroraReport>,
    private val throttleDuration: Duration,
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
            streamAuroraReports(locationUpdates(selectedPlace))
                .throttle(throttleDuration.toMillis())
                .collectLatest { report ->
                    state.update(selectedPlace, report)
                }
        }
    }

    private fun selectedPlace(state: UpdatableStateFlow<State>): Flow<Place> =
        state
            .map { it.selectedPlace }
            .distinctUntilChanged()

    private fun locationUpdates(selectedPlace: Place): Flow<Loadable<LocationResult>> =
        if (selectedPlace is Place.Favorite) {
            flowOf(Loadable.loaded(LocationResult.success(selectedPlace.location)))
        } else {
            currentLocation
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

// TODO replace with built-in
private fun <T> Flow<T>.throttle(waitMillis: Long): Flow<T> = flow {
    coroutineScope {
        val context = coroutineContext
        var nextMillis = 0L
        var delayPost: Deferred<Unit>? = null
        collect {
            val current = System.currentTimeMillis()
            if (nextMillis < current) {
                nextMillis = current + waitMillis
                emit(it)
                delayPost?.cancel()
            } else {
                val delayNext = nextMillis
                delayPost?.cancel()
                delayPost = async(Dispatchers.Default) {
                    delay(nextMillis - current)
                    if (delayNext == nextMillis) {
                        nextMillis = System.currentTimeMillis() + waitMillis
                        withContext(context) {
                            emit(it)
                        }
                    }
                }
            }
        }
    }
}
