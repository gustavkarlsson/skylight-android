package se.gustavkarlsson.skylight.android.feature.main

import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import se.gustavkarlsson.krate.core.Store
import se.gustavkarlsson.krate.core.dsl.buildStore
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.entities.Place
import se.gustavkarlsson.skylight.android.krate.Command
import se.gustavkarlsson.skylight.android.krate.Result
import se.gustavkarlsson.skylight.android.krate.State
import se.gustavkarlsson.skylight.android.lib.places.PlacesRepository
import timber.log.Timber

internal fun buildSkylightStore(
	permissionChecker: PermissionChecker,
	placesRepo: PlacesRepository,
	auroraReportProvider: AuroraReportProvider
): Store<State, Command, Result> {
	return buildStore {

		commands {
			transform<Command.Bootstrap> { commands ->
				commands.firstOrError()
					.flatMapPublisher { permissionChecker.permission }
					.map(Result::LocationPermission)
				// FIXME Can we refresh location when this happens?
			}

			transformWithState<Command.Bootstrap> { commands, getState ->
				commands.firstOrError()
					.flatMapPublisher { placesRepo.all() }
					.flatMap { places ->
						val state = getState()
						val oldPlaces = state.places
						val newSelection = state.selectedPlace?.let { selected ->
							findNewPlace(oldPlaces, places)
								?: findSamePlace(selected, places)
								?: Place.Current
						}
						selectPlace(newSelection, auroraReportProvider)
							.startWith(Result.Places(places))
					}
			}

			transformWithState<Command.RefreshAll> { commands, getState ->
				commands.switchMapSingle { _ ->
					val reportSingles = getState().places.map { place ->
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
							Result.AuroraReport.Success(reports.toMap()) as Result
						}
						.onErrorReturn { Result.AuroraReport.Failure(it) }
				}
			}
			/*
				FIXME handle settings commands
				transform<SettingsStreamCommand> { commands ->
					commands.switchMap { command ->
						if (command.stream) {
							Flowables.combineLatest(
								settings.notificationsEnabledChanges,
								settings.triggerLevelChanges
							) { notificationsEnabled, triggerLevel ->
								SettingsResult(
									State.Settings(notificationsEnabled, triggerLevel)
								)
							}
						} else {
							Flowable.empty()
						}
					}
				}
				*/
			transform<Command.SelectPlace> { commands ->
				commands.switchMap { (place) ->
					selectPlace(place, auroraReportProvider)
				}
			}
			if (BuildConfig.DEBUG) {
				watch<Command> { Timber.d("Got command: %s", it) }
			}

			watch<Command.AddPlace> {
				placesRepo.add(it.name, it.location)
			}

			watch<Command.RemovePlace> {
				placesRepo.remove(it.placeId)
			}

			watch<Command.SignalLocationPermissionDeniedForever> {
				permissionChecker.signalDeniedForever()
			}

			watch<Command.RefreshLocationPermission> {
				permissionChecker.refresh()
			}
		}

		results {

			reduce { state, result ->
				when (result) {
					is Result.LocationPermission -> {
						state.copy(locationPermission = result.permission)
					}
					is Result.AuroraReport.Success -> {
						val stateWithoutThrowable = state.copy(throwable = null)
						result.placesToAuroraReports.entries
							.fold(stateWithoutThrowable) { currentState, (place, report) ->
								currentState.updateReport(place, report)
							}
					}
					is Result.AuroraReport.Failure -> {
						state.copy(throwable = result.throwable)
					}
					/*
						FIXME Handle settings results
						is SettingsResult -> {
							state.copy(settings = result.settings)
						}
						*/
					is Result.PlaceSelected -> {
						state.copy(selectedPlace = result.place)
					}
					is Result.Places -> {
						state.copy(places = result.places)
					}
				}

			}
			if (BuildConfig.DEBUG) {
				watch<Result> { Timber.d("Got result: %s", it) }
			}
		}

		states {
			// FIXME Initialize from settings
			initial = State()
			observeScheduler = AndroidSchedulers.mainThread()
			if (BuildConfig.DEBUG) {
				watchAll { Timber.d("Got state: %s", it) }
			}
		}
	}
}

private fun selectPlace(
	place: Place?,
	auroraReportProvider: AuroraReportProvider
): Flowable<Result> {
	return if (place == null) {
		Flowable.just(Result.PlaceSelected(null))
	} else {
		val location = (place as? Place.Custom)?.location
		auroraReportProvider.stream(location)
			.map<Result> { result ->
				Result.AuroraReport.Success(mapOf(place to result))
			}
			.startWith(Result.PlaceSelected(place))
	}
}

private fun findNewPlace(oldPlaces: List<Place>, newPlaces: List<Place>): Place? =
	newPlaces.firstOrNull { it !in oldPlaces }

private fun findSamePlace(selectedPlace: Place?, newPlaces: List<Place>): Place? =
	selectedPlace?.takeIf { it in newPlaces }

private fun State.updateReport(place: Place, report: AuroraReport?): State {
	return if (report == null) {
		copy(auroraReports = auroraReports - place)
	} else {
		copy(auroraReports = auroraReports + (place to report))
	}
}
