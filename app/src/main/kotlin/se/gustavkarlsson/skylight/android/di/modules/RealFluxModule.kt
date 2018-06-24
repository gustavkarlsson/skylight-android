package se.gustavkarlsson.skylight.android.di.modules

import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import se.gustavkarlsson.flux.buildStore
import se.gustavkarlsson.skylight.android.BuildConfig
import se.gustavkarlsson.skylight.android.entities.ChanceLevel
import se.gustavkarlsson.skylight.android.flux.*
import se.gustavkarlsson.skylight.android.flux.SkylightState.Settings
import timber.log.Timber

class RealFluxModule(
	private val auroraReportModule: AuroraReportModule,
	private val connectivityModule: ConnectivityModule,
	private val settingsModule: SettingsModule
) : FluxModule {

	private fun createSettings(): Settings {
		return Settings(
			settingsModule.settings.notificationsEnabled,
			settingsModule.settings.triggerLevel
		)
	}

	override val store: SkylightStore by lazy {
		buildStore<SkylightState, SkylightCommand, SkylightResult> {
			initWith(SkylightState(createSettings()))

			switchMapCommand { _: GetAuroraReportCommand ->
				auroraReportModule.auroraReportProvider.get()
					.map<AuroraReportResult> { AuroraReportResult.Success(it) }
					.onErrorReturn { AuroraReportResult.Failure(it) }
					.toObservable()
					.startWith(AuroraReportResult.InFlight)
					.concatWith(
						Observable.just(
							AuroraReportResult.JustFinished,
							AuroraReportResult.Idle
						)
					)
			}
			switchMapCommand<AuroraReportStreamCommand, AuroraReportResult> { command ->
				if (command.stream) {
					auroraReportModule.auroraReportFlowable
						.map { AuroraReportResult.Success(it) as AuroraReportResult }
						.onErrorReturn { AuroraReportResult.Failure(it) }
						.toObservable()
				} else {
					Observable.just(AuroraReportResult.Idle)
				}
			}
			switchMapCommand { command: ConnectivityStreamCommand ->
				if (command.stream) {
					connectivityModule.connectivityFlowable
						.map(::ConnectivityResult)
						.toObservable()
				} else {
					Observable.empty()
				}
			}
			switchMapCommand { command: SettingsStreamCommand ->
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
					).toObservable()
				} else {
					Observable.empty()
				}
			}

			reduceResult { state, _: AuroraReportResult.JustFinished ->
				state.copy(isRefreshing = false, throwable = null, justFinishedRefreshing = true)
			}
			reduceResult { state, _: AuroraReportResult.Idle ->
				state.copy(isRefreshing = false, throwable = null, justFinishedRefreshing = false)
			}
			reduceResult { state, _: AuroraReportResult.InFlight ->
				state.copy(isRefreshing = true, throwable = null)
			}
			reduceResult { state, result: AuroraReportResult.Success ->
				state.copy(
					isRefreshing = false,
					throwable = null,
					auroraReport = result.auroraReport
				)
			}
			reduceResult { state, result: AuroraReportResult.Failure ->
				state.copy(isRefreshing = false, throwable = result.throwable)
			}
			reduceResult { state, result: ConnectivityResult ->
				state.copy(isConnectedToInternet = result.isConnectedToInternet)
			}
			reduceResult { state, result: SettingsResult ->
				state.copy(settings = result.settings)
			}

			observeOn(AndroidSchedulers.mainThread())

			if (BuildConfig.DEBUG) {
				doOnCommand<SkylightCommand> { Timber.d("Got command: $it") }
				doOnResult<SkylightResult> { Timber.d("Got result: $it") }
				doOnState { Timber.d("Got state: $it") }
			}
		}
	}
}
