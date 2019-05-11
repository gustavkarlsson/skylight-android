package se.gustavkarlsson.skylight.android.modules

import com.ioki.textref.TextRef
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import org.koin.dsl.module.module
import se.gustavkarlsson.krate.core.dsl.buildStore
import se.gustavkarlsson.skylight.android.BuildConfig
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.entities.CustomPlace
import se.gustavkarlsson.skylight.android.entities.Location
import se.gustavkarlsson.skylight.android.krate.Command
import se.gustavkarlsson.skylight.android.krate.Result
import se.gustavkarlsson.skylight.android.krate.State
import se.gustavkarlsson.skylight.android.krate.SkylightStore
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
		val auroraReports = get<Flowable<AuroraReport>>("auroraReport")

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

				transform<Command.GetAuroraReport> { commands ->
					commands.switchMap { _ ->
						auroraReportProvider.get()
							.map<Result> { Result.AuroraReport.Success(it) }
							.onErrorReturn { Result.AuroraReport.Failure(it) }
							.toFlowable()
					}
				}
				transform<Command.AuroraReportStream> { commands ->
					commands
						.map(Command.AuroraReportStream::stream)
						.distinctUntilChanged()
						.switchMap { stream ->
							if (stream) {
								auroraReports
									.map<Result> { Result.AuroraReport.Success(it) }
									.onErrorReturn { Result.AuroraReport.Failure(it) }
							} else {
								Flowable.empty()
							}
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
					commands.map { Result.PlaceSelected(it.place) }
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
							state.copy(
								throwable = null,
								currentPlace = state.currentPlace.copy(auroraReport = result.auroraReport)
							)
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
						CustomPlace(
							1,
							TextRef("Made-up"),
							Location(1.0, 2.0)
						)
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
