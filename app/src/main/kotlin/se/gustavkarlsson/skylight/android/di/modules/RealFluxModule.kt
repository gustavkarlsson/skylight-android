package se.gustavkarlsson.skylight.android.di.modules

import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.rxkotlin.ofType
import se.gustavkarlsson.skylight.android.flux.*

class RealFluxModule(
	private val auroraReportModule: AuroraReportModule,
	private val connectivityModule: ConnectivityModule
) : FluxModule {
	override val store: SkylightStore by lazy {
		SkylightStore(State(), transformers, accumulator)
	}

	private val transformers: Iterable<ObservableTransformer<Action, Result>> by lazy {
		listOf(
			getAuroraReportActionToResult,
			streamAuroraReportsActionToResult,
			streamConnectivityActionToResult
		)
	}

	private val getAuroraReportActionToResult: ObservableTransformer<Action, Result> by lazy {
		ObservableTransformer<Action, Result> {
			it.ofType<GetAuroraReportAction>().switchMap {
				auroraReportModule.auroraReportSingle
					.map<AuroraReportResult> { AuroraReportResult.Success(it) }
					.onErrorReturn { AuroraReportResult.Failure(it) }
					.toObservable()
					.startWith(AuroraReportResult.InFlight)
					.concatWith(Observable.just(AuroraReportResult.Idle))
			}
		}
	}

	private val streamAuroraReportsActionToResult: ObservableTransformer<Action, Result> by lazy {
		ObservableTransformer<Action, Result> {
			it.ofType<AuroraReportStreamAction>().switchMap {
				if (it.stream) {
					auroraReportModule.auroraReportFlowable
						.map<AuroraReportResult> { AuroraReportResult.Success(it) }
						.onErrorReturn { AuroraReportResult.Failure(it) }
						.toObservable()
						.concatWith(Observable.just(AuroraReportResult.Idle))
				} else {
					Observable.just(AuroraReportResult.Idle)
				}
			}
		}
	}

	private val streamConnectivityActionToResult: ObservableTransformer<Action, Result> by lazy {
		ObservableTransformer<Action, Result> {
			it.ofType<ConnectivityStreamAction>().switchMap {
				connectivityModule.connectivityFlowable
					.map(::ConnectivityResult)
					.toObservable()
			}
		}
	}

	private val accumulator: (current: State, result: Result) -> State by lazy {
		{ current: State, result: Result ->
			when (result) {
				is AuroraReportResult.Idle -> current.copy(
					throwable = null
				)
				is AuroraReportResult.InFlight -> current.copy(
					isRefreshing = true,
					throwable = null
				)
				is AuroraReportResult.Success -> current.copy(
					isRefreshing = false,
					auroraReport = result.auroraReport,
					throwable = null
				)
				is AuroraReportResult.Failure -> current.copy(
					isRefreshing = false,
					throwable = result.throwable
				)
				is ConnectivityResult -> current.copy(
					isConnectedToInternet = result.isConnectedToInternet
				)
			}
		}
	}
}
