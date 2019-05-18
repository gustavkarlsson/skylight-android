package se.gustavkarlsson.skylight.android.modules

import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import org.koin.dsl.module.module
import se.gustavkarlsson.krate.core.dsl.buildStore
import se.gustavkarlsson.skylight.android.BuildConfig
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.entities.Place
import se.gustavkarlsson.skylight.android.krate.Command
import se.gustavkarlsson.skylight.android.krate.Result
import se.gustavkarlsson.skylight.android.krate.SkylightStore
import se.gustavkarlsson.skylight.android.krate.State
import se.gustavkarlsson.skylight.android.services.GooglePlayServicesChecker
import se.gustavkarlsson.skylight.android.services.PermissionChecker
import se.gustavkarlsson.skylight.android.services.PlacesRepository
import se.gustavkarlsson.skylight.android.services.RunVersionManager
import se.gustavkarlsson.skylight.android.services.providers.AuroraReportProvider
import timber.log.Timber

val krateModule = module {

	single { _ ->
		val runVersionManager = get<RunVersionManager>()
		val googlePlayServicesChecker = get<GooglePlayServicesChecker>()
		val permissionChecker = get<PermissionChecker>()
		val auroraReportProvider = get<AuroraReportProvider>()
		val placesRepo = get<PlacesRepository>()

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

				transform<Command.Bootstrap> { commands ->
					commands.firstOrError()
						.flatMapPublisher { placesRepo.all() }
						.map(Result::Places)
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
					commands
						.distinctUntilChanged()
						.switchMap { (place) ->
							if (place == null) {
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

	single("state") {
		get<SkylightStore>().states
	}

}

private fun State.updateReport(place: Place, report: AuroraReport?): State {
	return if (report == null) {
		copy(auroraReports = auroraReports - place)
	} else {
		copy(auroraReports = auroraReports + (place to report))
	}
}
