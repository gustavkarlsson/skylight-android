package se.gustavkarlsson.skylight.android.modules

import com.ioki.textref.TextRef
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import org.koin.dsl.module.module
import se.gustavkarlsson.krate.core.dsl.buildStore
import se.gustavkarlsson.skylight.android.BuildConfig
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.entities.Location
import se.gustavkarlsson.skylight.android.entities.Place
import se.gustavkarlsson.skylight.android.krate.Command
import se.gustavkarlsson.skylight.android.krate.Result
import se.gustavkarlsson.skylight.android.krate.SkylightStore
import se.gustavkarlsson.skylight.android.krate.State
import se.gustavkarlsson.skylight.android.services.GooglePlayServicesChecker
import se.gustavkarlsson.skylight.android.services.PermissionChecker
import se.gustavkarlsson.skylight.android.services.RunVersionManager
import se.gustavkarlsson.skylight.android.services.providers.AuroraReportProvider
import timber.log.Timber

val krateModule = module {

	single { _ ->
		val runVersionManager = get<RunVersionManager>()
		val googlePlayServicesChecker = get<GooglePlayServicesChecker>()
		val permissionChecker = get<PermissionChecker>()
		val auroraReportProvider = get<AuroraReportProvider>()

		buildStore<State, Command, Result> {

			commands {
				transform<Command.Bootstrap> { commands ->
					commands.firstOrError()
						.flatMapPublisher { permissionChecker.isLocationGranted }
						.map(Result::LocationPermission)
				}

				transform<Command.Bootstrap> { commands ->
					commands.firstOrError()
						.flatMapPublisher { googlePlayServicesChecker.isAvailable }
						.map(Result::GooglePlayServices)
				}

				transform<Command.Bootstrap> { commands ->
					commands.firstOrError()
						.flatMapPublisher { runVersionManager.isFirstRun }
						.map(Result::FirstRun)
				}

				transformWithState<Command.RefreshAll> { commands, getState ->
					commands.switchMapSingle { _ ->
						val reportSingles = getState().allPlaces.map { place ->
							when (place) {
								is Place.Current -> auroraReportProvider.get(null)
								is Place.Custom -> auroraReportProvider.get(place.location)
							}.map { place.id to it }
						}
						Single
							.zip(reportSingles) { reportsArray ->
								val reports = reportsArray
									.asList() as List<Pair<Int, AuroraReport>>
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
				transformWithState<Command.SelectPlace> { commands, getState ->
					commands
						.distinctUntilChanged()
						.switchMap { (placeId) ->
							if (placeId == null) {
								Flowable.just(Result.PlaceSelected(placeId))
							} else {
								val place = getState().allPlaces.find { it.id == placeId }
									?: throw IllegalStateException("No place in state with id=$placeId")
								val location = (place as? Place.Custom)?.location
								auroraReportProvider.stream(location)
									.map<Result> { result ->
										Result.AuroraReport.Success(mapOf(placeId to result))
									}
									.startWith(Result.PlaceSelected(placeId))
							}
						}
				}
				if (BuildConfig.DEBUG) {
					watch<Command> { Timber.d("Got command: %s", it) }
				}

				watch<Command.SignalLocationPermissionGranted> {
					permissionChecker.signalPermissionGranted()
				}

				watch<Command.SignalFirstRunCompleted> {
					runVersionManager.signalFirstRunCompleted()
				}

				watch<Command.SignalGooglePlayServicesInstalled> {
					googlePlayServicesChecker.signalInstalled()
				}
			}

			results {

				reduce { state, result ->
					when (result) {
						is Result.LocationPermission -> {
							state.copy(isLocationPermissionGranted = result.isGranted)
						}
						is Result.GooglePlayServices -> {
							state.copy(isGooglePlayServicesAvailable = result.isAvailable)
						}
						is Result.FirstRun -> {
							state.copy(isFirstRun = result.isFirstRun)
						}
						is Result.AuroraReport.Success -> {
							val stateWithoutThrowable = state.copy(throwable = null)
							result.placeIdsToAuroraReports.entries
								.fold(stateWithoutThrowable) { currentState, (placeId, report) ->
									currentState.updateReport(placeId, report)
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
							state.copy(selectedPlaceId = result.placeId)
						}
					}

				}
				if (BuildConfig.DEBUG) {
					watch<Result> { Timber.d("Got result: %s", it) }
				}
			}

			states {
				// FIXME Initialize from settings
				initial = State(
					customPlaces = listOf(
						Place.Custom(1, TextRef("Home"), Location(64.3753, 19.4007)),
						Place.Custom(2, TextRef("Spain"), Location(40.4168, -3.7038)),
						Place.Custom(3, TextRef("The Big Apple"), Location(40.7128, -74.0060))
					) // FIXME Remove
				)
				observeScheduler = AndroidSchedulers.mainThread()
				if (BuildConfig.DEBUG) {
					watchAll { Timber.d("Got state: %s", it) }
				}
			}
		}
	}

	single("state") {
		get<SkylightStore>().states
	}

}

private fun State.updateReport(placeId: Int, report: AuroraReport?): State {
	return if (placeId == Place.Current.ID) {
		copy(currentPlace = currentPlace.copy(auroraReport = report))
	} else {
		val newPlaces = customPlaces.map {
			if (placeId == it.id)
				it.copy(auroraReport = report)
			else it
		}
		copy(customPlaces = newPlaces)
	}
}
