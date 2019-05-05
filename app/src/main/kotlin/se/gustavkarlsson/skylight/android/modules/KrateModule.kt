package se.gustavkarlsson.skylight.android.modules

import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.Flowables
import org.koin.dsl.module.module
import se.gustavkarlsson.krate.core.dsl.buildStore
import se.gustavkarlsson.skylight.android.BuildConfig
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.entities.Location
import se.gustavkarlsson.skylight.android.entities.Place
import se.gustavkarlsson.skylight.android.krate.AuroraReportResult
import se.gustavkarlsson.skylight.android.krate.AuroraReportStreamCommand
import se.gustavkarlsson.skylight.android.krate.BootstrapCommand
import se.gustavkarlsson.skylight.android.krate.FirstRunResult
import se.gustavkarlsson.skylight.android.krate.GetAuroraReportCommand
import se.gustavkarlsson.skylight.android.krate.GooglePlayServicesResult
import se.gustavkarlsson.skylight.android.krate.LocationPermissionResult
import se.gustavkarlsson.skylight.android.krate.PlaceSelectedResult
import se.gustavkarlsson.skylight.android.krate.SelectPlaceCommand
import se.gustavkarlsson.skylight.android.krate.SettingsResult
import se.gustavkarlsson.skylight.android.krate.SettingsStreamCommand
import se.gustavkarlsson.skylight.android.krate.SignalFirstRunCompleted
import se.gustavkarlsson.skylight.android.krate.SignalGooglePlayServicesInstalled
import se.gustavkarlsson.skylight.android.krate.SignalLocationPermissionGranted
import se.gustavkarlsson.skylight.android.krate.SkylightCommand
import se.gustavkarlsson.skylight.android.krate.SkylightResult
import se.gustavkarlsson.skylight.android.krate.SkylightState
import se.gustavkarlsson.skylight.android.krate.SkylightStore
import se.gustavkarlsson.skylight.android.services.GooglePlayServicesChecker
import se.gustavkarlsson.skylight.android.services.PermissionChecker
import se.gustavkarlsson.skylight.android.services.RunVersionManager
import se.gustavkarlsson.skylight.android.services.Settings
import se.gustavkarlsson.skylight.android.services.providers.AuroraReportProvider
import timber.log.Timber

val krateModule = module {

	single { _ ->
		val settings = get<Settings>()
		val runVersionManager = get<RunVersionManager>()
		val googlePlayServicesChecker = get<GooglePlayServicesChecker>()
		val permissionChecker = get<PermissionChecker>()
		val auroraReportProvider = get<AuroraReportProvider>()
		val auroraReports = get<Flowable<AuroraReport>>("auroraReport")

		buildStore<SkylightState, SkylightCommand, SkylightResult> {

			commands {
				transform<BootstrapCommand> { commands ->
					commands.firstOrError()
						.flatMapPublisher { permissionChecker.isLocationGranted }
						.map(::LocationPermissionResult)
				}

				transform<BootstrapCommand> { commands ->
					commands.firstOrError()
						.flatMapPublisher { googlePlayServicesChecker.isAvailable }
						.map(::GooglePlayServicesResult)
				}

				transform<BootstrapCommand> { commands ->
					commands.firstOrError()
						.flatMapPublisher { runVersionManager.isFirstRun }
						.map(::FirstRunResult)
				}

				transform<GetAuroraReportCommand> { commands ->
					commands.switchMap { _ ->
						auroraReportProvider.get()
							.map<AuroraReportResult> { AuroraReportResult.Success(it) }
							.onErrorReturn { AuroraReportResult.Failure(it) }
							.toFlowable()
					}
				}
				transform<AuroraReportStreamCommand> { commands ->
					commands
						.map(AuroraReportStreamCommand::stream)
						.distinctUntilChanged()
						.switchMap { stream ->
						if (stream) {
							auroraReports
								.map { AuroraReportResult.Success(it) as AuroraReportResult }
								.onErrorReturn { AuroraReportResult.Failure(it) }
						} else {
							Flowable.empty()
						}
					}
				}
				transform<SettingsStreamCommand> { commands ->
					commands.switchMap { command ->
						if (command.stream) {
							Flowables.combineLatest(
								settings.notificationsEnabledChanges,
								settings.triggerLevelChanges
							) { notificationsEnabled, triggerLevel ->
								SettingsResult(
									SkylightState.Settings(notificationsEnabled, triggerLevel)
								)
							}
						} else {
							Flowable.empty()
						}
					}
				}
				transform<SelectPlaceCommand> { commands ->
					commands.map { PlaceSelectedResult(it.place) }
				}
				if (BuildConfig.DEBUG) {
					watch<SkylightCommand> { Timber.d("Got command: %s", it) }
				}

				watch<SignalLocationPermissionGranted> {
					permissionChecker.signalPermissionGranted()
				}

				watch<SignalFirstRunCompleted> {
					runVersionManager.signalFirstRunCompleted()
				}

				watch<SignalGooglePlayServicesInstalled> {
					googlePlayServicesChecker.signalInstalled()
				}
			}

			results {

				reduce { state, result ->
					when (result) {
						is LocationPermissionResult -> {
							state.copy(isLocationPermissionGranted = result.isGranted)
						}
						is GooglePlayServicesResult -> {
							state.copy(isGooglePlayServicesAvailable = result.isAvailable)
						}
						is FirstRunResult -> {
							state.copy(isFirstRun = result.isFirstRun)
						}
						is AuroraReportResult.Success -> {
							state.copy(
								throwable = null,
								currentLocationAuroraReport = result.auroraReport
							)
						}
						is AuroraReportResult.Failure -> {
							state.copy(throwable = result.throwable)
						}
						is SettingsResult -> {
							state.copy(settings = result.settings)
						}
						is PlaceSelectedResult -> {
							state.copy(selectedPlace = result.place)
						}
					}

				}
				if (BuildConfig.DEBUG) {
					watch<SkylightResult> { Timber.d("Got result: %s", it) }
				}
			}

			states {
				initial = SkylightState(
					SkylightState.Settings(
						settings.notificationsEnabled,
						settings.triggerLevel
					),
					customPlaces = listOf(Place(1, "Made-up", Location(1.0, 2.0))) // FIXME Remove
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
