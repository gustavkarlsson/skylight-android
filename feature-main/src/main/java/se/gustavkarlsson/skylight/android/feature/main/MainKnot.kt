package se.gustavkarlsson.skylight.android.feature.main

import de.halfbit.knot.Knot
import de.halfbit.knot.knot
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import se.gustavkarlsson.skylight.android.entities.Loadable
import se.gustavkarlsson.skylight.android.entities.LoadableAuroraReport
import se.gustavkarlsson.skylight.android.entities.LocationResult
import se.gustavkarlsson.skylight.android.entities.Permission
import se.gustavkarlsson.skylight.android.lib.location.LocationProvider
import se.gustavkarlsson.skylight.android.lib.permissions.PermissionChecker
import se.gustavkarlsson.skylight.android.lib.places.Place
import se.gustavkarlsson.skylight.android.lib.places.SelectedPlaceRepository
import timber.log.Timber

internal data class State(
	val isStreaming: Boolean = false,
	val locationPermission: Permission = Permission.Unknown,
	val selectedPlace: Place,
	val selectedAuroraReport: LoadableAuroraReport = LoadableAuroraReport.LOADING
)

internal sealed class Change {
	data class PlaceSelected(val place: Place) : Change()
	data class StreamToggle(val stream: Boolean) : Change()
	data class LocationPermission(val permission: Permission) : Change()
	data class AuroraReportSuccess(val place: Place, val report: LoadableAuroraReport) : Change()
	data class AuroraReportFailure(val throwable: Throwable) : Change()
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
			selectedPlace = selectedPlaceRepo.selected.blockingFirst()
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
					copy(locationPermission = change.permission).only
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
					} else {
						this.only
					}
				is Change.AuroraReportFailure -> TODO("FIXME Take care of errors")
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
					val locationUpdates: Flowable<Loadable<LocationResult>> =
						if (place is Place.Custom) {
							Flowable.just(Loadable.Loaded(LocationResult.Success(place.location)))
						} else locationProvider.stream()
					auroraReportProvider.stream(locationUpdates)
						.toObservable()
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
			permissionChecker.permission
				.toObservable()
				.map(Change::LocationPermission)
		}
		source {
			selectedPlaceRepo.selected
				.map(Change::PlaceSelected)
		}
	}
}
