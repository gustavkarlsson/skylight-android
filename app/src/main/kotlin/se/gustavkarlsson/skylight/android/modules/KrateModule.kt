package se.gustavkarlsson.skylight.android.modules

import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import org.koin.dsl.module.module
import se.gustavkarlsson.krate.core.dsl.buildStore
import se.gustavkarlsson.skylight.android.BuildConfig
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.entities.ChanceLevel
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
							.startWith(AuroraReportResult.InFlight)
							.concatWith(
								Flowable.just(
									AuroraReportResult.JustFinished,
									AuroraReportResult.Idle
								)
							)
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
							Flowable.just(AuroraReportResult.Idle)
						}
					}
				}
				transform<SettingsStreamCommand> { commands ->
					commands.switchMap { command ->
						if (command.stream) {
							Flowable.combineLatest(
								settings.notificationsEnabledChanges,
								settings.triggerLevelChanges,
								BiFunction { notificationsEnabled: Boolean, triggerLevel: ChanceLevel ->
									SettingsResult(
										SkylightState.Settings(
											notificationsEnabled,
											triggerLevel
										)
									)
								}
							)
						} else {
							Flowable.empty()
						}
					}
				}
				if (BuildConfig.DEBUG) {
					watch<SkylightCommand> { Timber.d("Got command: $it") }
				}
			}

			results {
				reduce<LocationPermissionResult> { state, result ->
					state.copy(isLocationPermissionGranted = result.isGranted)
				}
				reduce<GooglePlayServicesResult> { state, result ->
					state.copy(isGooglePlayServicesAvailable = result.isAvailable)
				}
				reduce<FirstRunResult> { state, result ->
					state.copy(isFirstRun = result.isFirstRun)
				}
				reduce<AuroraReportResult.JustFinished> { state, _ ->
					state.copy(
						isRefreshing = false,
						throwable = null,
						justFinishedRefreshing = true
					)
				}
				reduce<AuroraReportResult.Idle> { state, _ ->
					state.copy(
						isRefreshing = false,
						throwable = null,
						justFinishedRefreshing = false
					)
				}
				reduce<AuroraReportResult.InFlight> { state, _ ->
					state.copy(isRefreshing = true, throwable = null)
				}
				reduce<AuroraReportResult.Success> { state, result ->
					state.copy(
						isRefreshing = false,
						throwable = null,
						auroraReport = result.auroraReport
					)
				}
				reduce<AuroraReportResult.Failure> { state, result ->
					state.copy(isRefreshing = false, throwable = result.throwable)
				}
				reduce<SettingsResult> { state, result ->
					state.copy(settings = result.settings)
				}
				if (BuildConfig.DEBUG) {
					watch<SkylightResult> { Timber.d("Got result: $it") }
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
					watchAll { Timber.d("Got state: $it") }
				}
			}
		}
	}

	single("state") {
		get<SkylightStore>().states
	}

}
