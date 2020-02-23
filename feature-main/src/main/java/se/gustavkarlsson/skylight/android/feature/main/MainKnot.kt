package se.gustavkarlsson.skylight.android.feature.main

import de.halfbit.knot.Knot
import de.halfbit.knot.knot
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import se.gustavkarlsson.skylight.android.entities.Loadable
import se.gustavkarlsson.skylight.android.entities.LoadableAuroraReport
import se.gustavkarlsson.skylight.android.entities.LocationResult
import se.gustavkarlsson.skylight.android.entities.Access
import se.gustavkarlsson.skylight.android.services.LocationProvider
import se.gustavkarlsson.skylight.android.services.PermissionChecker
import se.gustavkarlsson.skylight.android.entities.Place
import se.gustavkarlsson.skylight.android.services.AuroraReportProvider
import se.gustavkarlsson.skylight.android.services.SelectedPlaceRepository
import timber.log.Timber

internal data class State(
    val isStreaming: Boolean = false,
    val locationAccess: Access = Access.Unknown,
    val selectedPlace: Place,
    val selectedAuroraReport: LoadableAuroraReport = LoadableAuroraReport.LOADING
)

internal sealed class Change {
    data class PlaceSelected(val place: Place) : Change()
    data class StreamToggle(val stream: Boolean) : Change()
    data class LocationPermission(val access: Access) : Change()
    data class AuroraReportSuccess(val place: Place, val report: LoadableAuroraReport) : Change()
}

private sealed class Action {
    data class Stream(val stream: Boolean, val place: Place?) : Action()
}

internal fun buildMainKnot(
    permissionChecker: PermissionChecker,
    selectedPlaceRepo: SelectedPlaceRepository,
    locationProvider: LocationProvider,
    auroraReportProvider: AuroraReportProvider
): Knot<State, Change> = knot<State, Change, Action> {

    state {
        initial = State(
            selectedPlace = selectedPlaceRepo.get()
        )
        observeOn = AndroidSchedulers.mainThread()
        if (BuildConfig.DEBUG) {
            watchAll { Timber.d("Got state: %s", it) }
        }
    }

    changes {
        reduce { change ->
            when (change) {
                is Change.LocationPermission ->
                    copy(locationAccess = change.access).only
                is Change.PlaceSelected ->
                    copy(
                        selectedPlace = change.place,
                        selectedAuroraReport = LoadableAuroraReport.LOADING // TODO Get from cache?
                    ) + Action.Stream(isStreaming, change.place)
                is Change.StreamToggle ->
                    copy(isStreaming = change.stream) +
                        Action.Stream(change.stream, selectedPlace)
                is Change.AuroraReportSuccess ->
                    if (selectedPlace == change.place) {
                        copy(selectedAuroraReport = change.report).only
                    } else this.only
            }
        }

        if (BuildConfig.DEBUG) {
            watchAll { Timber.d("Got change: %s", it) }
        }
    }

    actions {
        perform<Action.Stream> {
            distinctUntilChanged()
            switchMap { (stream, place) ->
                if (stream && place != null) {
                    val locationUpdates: Observable<Loadable<LocationResult>> =
                        if (place is Place.Custom) {
                            Observable.just(Loadable.Loaded(LocationResult.Success(place.location)))
                        } else locationProvider.stream()
                    auroraReportProvider.stream(locationUpdates)
                        .map<Change> { result ->
                            Change.AuroraReportSuccess(place, result)
                        }
                } else {
                    Observable.empty()
                }
            }
        }

        if (BuildConfig.DEBUG) {
            watchAll { Timber.d("Got action: %s", it) }
        }
    }

    events {
        source {
            permissionChecker.access
                .map(Change::LocationPermission)
        }
        source {
            selectedPlaceRepo.stream()
                .map(Change::PlaceSelected)
        }
    }
}
