package se.gustavkarlsson.skylight.android.feature.main

import de.halfbit.knot.Knot
import de.halfbit.knot.knot
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.entities.Permission
import se.gustavkarlsson.skylight.android.entities.Place
import se.gustavkarlsson.skylight.android.lib.permissions.PermissionChecker
import se.gustavkarlsson.skylight.android.lib.places.PlacesRepository
import timber.log.Timber

internal data class State(
	val isStreaming: Boolean = false,
	val locationPermission: Permission = Permission.Unknown,
	val places: List<Place> = emptyList(),
	val selectedPlace: Place? = null,
	val auroraReports: Map<Place, AuroraReport> = emptyMap()
) {
	val selectedAuroraReport: AuroraReport? get() = auroraReports[selectedPlace]
}

internal sealed class Change {
	data class SelectPlace(val place: Place) : Change()
	data class Stream(val stream: Boolean) : Change()
	data class LocationPermission(val permission: Permission) : Change()
	data class Places(val places: List<Place>) : Change()
	object RefreshAll : Change()
	data class AuroraReportSuccess(val placesToAuroraReports: Map<Place, AuroraReport?>) : Change()
	data class AuroraReportFailure(val throwable: Throwable) : Change()
}

private sealed class Action {
	data class Stream(val stream: Boolean, val place: Place?) : Action()
	data class Refresh(val places: List<Place>) : Action()
}

internal fun buildMainKnot(
	permissionChecker: PermissionChecker,
	placesRepo: PlacesRepository,
	auroraReportProvider: AuroraReportProvider
): Knot<State, Change> = knot<State, Change, Action> {

	state {
		initial = State()
		observeOn = AndroidSchedulers.mainThread()
		if (BuildConfig.DEBUG) {
			watchAll { Timber.d("Got state: %s", it) }
		}
	}

	changes {
		reduce { change ->
			when (change) {
				is Change.LocationPermission -> copy(locationPermission = change.permission).only
				is Change.Places -> {
					val newSelection = findNewPlace(
						places,
						change.places
					)
						?: findSamePlace(selectedPlace, change.places)
						?: Place.Current
					copy(places = change.places, selectedPlace = newSelection) +
						Action.Stream(isStreaming, newSelection)
				}
				is Change.SelectPlace -> copy(selectedPlace = change.place) + Action.Stream(
					isStreaming,
					change.place
				)
				is Change.Stream -> copy(isStreaming = change.stream) + Action.Stream(
					change.stream,
					selectedPlace
				)
				is Change.AuroraReportSuccess -> {
					change.placesToAuroraReports.entries
						.fold(this) { state, (place, report) ->
							state.updateReport(place, report)
						}.only
				}
				is Change.AuroraReportFailure -> TODO("FIXME Take care of errors")
				Change.RefreshAll -> this + Action.Refresh(
					places
				)
			}
		}

		if (BuildConfig.DEBUG) {
			watchAll { Timber.d("Got change: %s", it) }
		}
	}

	actions {
		perform<Action.Stream> {
			distinctUntilChanged()
			flatMap { (stream, place) ->
				if (stream && place != null) {
					val locationToStream = (place as? Place.Custom)?.location
					auroraReportProvider.stream(locationToStream)
						.toObservable()
						.map<Change> { result ->
							Change.AuroraReportSuccess(mapOf(place to result))
						}
				} else {
					Observable.empty()
				}
			}
		}

		perform<Action.Refresh> {
			switchMapSingle { (places) ->
				val reportSingles = places.map { place ->
					when (place) {
						is Place.Current -> auroraReportProvider.get(null)
						is Place.Custom -> auroraReportProvider.get(place.location)
					}.map { place to it }
				}
				Single
					.zip(reportSingles) { reportsArray ->
						@Suppress("UNCHECKED_CAST")
						val reports = reportsArray
							.asList() as List<Pair<Place, AuroraReport>>
						Change.AuroraReportSuccess(reports.toMap()) as Change
					}
					.onErrorReturn { Change.AuroraReportFailure(it) }
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
			placesRepo.all()
				.toObservable()
				.map(Change::Places)
		}
	}
}

// FIXME instead this, store selected place locally (in cache)
private fun findNewPlace(oldPlaces: List<Place>, newPlaces: List<Place>): Place? =
	newPlaces.lastOrNull { it !in oldPlaces }

private fun findSamePlace(selectedPlace: Place?, newPlaces: List<Place>): Place? =
	selectedPlace?.takeIf { it in newPlaces }

private fun State.updateReport(place: Place, report: AuroraReport?): State {
	return if (report == null) {
		copy(auroraReports = auroraReports - place)
	} else {
		copy(auroraReports = auroraReports + (place to report))
	}
}
