package se.gustavkarlsson.skylight.android.modules

import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.Flowables
import org.koin.dsl.module.module
import se.gustavkarlsson.krate.core.dsl.buildStore
import se.gustavkarlsson.skylight.android.BuildConfig
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.krate.AuroraReportResult
import se.gustavkarlsson.skylight.android.krate.AuroraReportStreamCommand
import se.gustavkarlsson.skylight.android.krate.BootstrapCommand
import se.gustavkarlsson.skylight.android.krate.FirstRunResult
import se.gustavkarlsson.skylight.android.krate.GetAuroraReportCommand
import se.gustavkarlsson.skylight.android.krate.GooglePlayServicesResult
import se.gustavkarlsson.skylight.android.krate.LocationPermissionResult
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
						.map<SkylightResult> {
							LocationPermissionResult(permissionChecker.isLocationGranted)
						}
						.toFlowable()
				}

				transform<BootstrapCommand> { commands ->
					commands.firstOrError()
						.map<SkylightResult> {
							GooglePlayServicesResult(googlePlayServicesChecker.isAvailable)
						}
						.toFlowable()
				}

				transform<BootstrapCommand> { commands ->
					commands.firstOrError()
						.map<SkylightResult> {
							FirstRunResult(runVersionManager.isFirstRun)
						}
						.toFlowable()
				}

				transform<SignalFirstRunCompleted> { commands ->
					commands
						.doOnNext {
							runVersionManager.signalFirstRunCompleted()
						}
						.map {
							FirstRunResult(false)
						}
				}

				transform<SignalLocationPermissionGranted> { commands ->
					commands.map {
						LocationPermissionResult(true)
					}
				}

				transform<SignalGooglePlayServicesInstalled> { commands ->
					commands.map {
						GooglePlayServicesResult(true)
					}
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
				if (BuildConfig.DEBUG) {
					watch<SkylightCommand> { Timber.d("Got command: %s", it) }
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
								auroraReport = result.auroraReport
							)
						}
						is AuroraReportResult.Failure -> {
							state.copy(throwable = result.throwable)
						}
						is SettingsResult -> {
							state.copy(settings = result.settings)
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
					)
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
