package se.gustavkarlsson.skylight.android.di.modules

import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import se.gustavkarlsson.krate.core.dsl.buildStore
import se.gustavkarlsson.skylight.android.BuildConfig
import se.gustavkarlsson.skylight.android.entities.ChanceLevel
import se.gustavkarlsson.skylight.android.krate.*
import se.gustavkarlsson.skylight.android.krate.SkylightState.LocationPermission
import se.gustavkarlsson.skylight.android.krate.SkylightState.Settings
import timber.log.Timber

class RealKrateModule(
	private val auroraReportModule: AuroraReportModule,
	private val connectivityModule: ConnectivityModule,
	private val settingsModule: SettingsModule
) : KrateModule {

	private fun createSettings(): Settings {
		return Settings(
			settingsModule.settings.notificationsEnabled,
			settingsModule.settings.triggerLevel
		)
	}

	override val store: SkylightStore by lazy {
		buildStore<SkylightState, SkylightCommand, SkylightResult> {

			states {
				initial = SkylightState(createSettings())
				observeScheduler = AndroidSchedulers.mainThread()
				if (BuildConfig.DEBUG) {
					watchAll { Timber.d("Got state: $it") }
				}
			}

			commands {
				transform<GetAuroraReportCommand> { commands ->
					commands.switchMap {
						auroraReportModule.auroraReportProvider.get()
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
					commands.switchMap { command ->
						if (command.stream) {
							auroraReportModule.auroraReportFlowable
								.map { AuroraReportResult.Success(it) as AuroraReportResult }
								.onErrorReturn { AuroraReportResult.Failure(it) }
						} else {
							Flowable.just(AuroraReportResult.Idle)
						}
					}
				}
				transform<ConnectivityStreamCommand> { commands ->
					commands.switchMap { command ->
						if (command.stream) {
							connectivityModule.connectivityFlowable
								.map(::ConnectivityResult)
						} else {
							Flowable.empty()
						}
					}
				}
				transform<SettingsStreamCommand> { commands ->
					commands.switchMap { command ->
						if (command.stream) {
							Flowable.combineLatest(
								settingsModule.settings.notificationsEnabledChanges,
								settingsModule.settings.triggerLevelChanges,
								BiFunction { notificationsEnabled: Boolean, triggerLevel: ChanceLevel ->
									SettingsResult(
										Settings(
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
				transform<SetLocationPermissionGrantedCommand> { commands ->
					commands.map {
						LocationPermissionGrantedResult
					}
				}
				if (BuildConfig.DEBUG) {
					watch<SkylightCommand> { Timber.d("Got command: $it") }
				}
			}

			results {
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
				reduce<ConnectivityResult> { state, result ->
					state.copy(isConnectedToInternet = result.isConnectedToInternet)
				}
				reduce<LocationPermissionGrantedResult> { state, _ ->
					state.copy(locationPermission = LocationPermission.GRANTED)
				}
				reduce<SettingsResult> { state, result ->
					state.copy(settings = result.settings)
				}
				if (BuildConfig.DEBUG) {
					watch<SkylightResult> { Timber.d("Got result: $it") }
				}
			}
		}
	}
}
