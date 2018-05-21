package se.gustavkarlsson.skylight.android.di.modules

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import se.gustavkarlsson.skylight.android.flux.*

class RealFluxModule(
	private val auroraReportModule: AuroraReportModule,
	private val connectivityModule: ConnectivityModule
) : FluxModule {
	override val store: SkylightStore by lazy {
		buildStore<SkylightState, SkylightAction, SkylightResult> {
			setInitialState(SkylightState())
			switchMapAction(::getAuroraReport)
			switchMapAction(::streamAuroraReports)
			switchMapAction(::streamConnectivity)
			mapAction(::showDialog)
			mapAction(::hideDialog)
			reduce { state, _: AuroraReportResult.Idle ->
				state.copy(throwable = null)
			}
			reduce { state, _: AuroraReportResult.InFlight ->
				state.copy(isRefreshing = true, throwable = null)
			}
			reduce { state, result: AuroraReportResult.Success ->
				state.copy(isRefreshing = false, throwable = null, auroraReport = result.auroraReport)
			}
			reduce { state, result: AuroraReportResult.Failure ->
				state.copy(isRefreshing = false, throwable = result.throwable)
			}
			reduce { state, result: ConnectivityResult ->
				state.copy(isConnectedToInternet = result.isConnectedToInternet)
			}
			reduce { state, result: DialogResult ->
				state.copy(dialog = result.dialog)
			}
			setObserveScheduler(AndroidSchedulers.mainThread())
			addStartActions(GetAuroraReportAction)
		}
	}

	private fun getAuroraReport(action: GetAuroraReportAction): Observable<AuroraReportResult> =
		auroraReportModule.auroraReportSingle
			.map<AuroraReportResult> { AuroraReportResult.Success(it) }
			.onErrorReturn { AuroraReportResult.Failure(it) }
			.toObservable()
			.startWith(AuroraReportResult.InFlight)
			.concatWith(Observable.just(AuroraReportResult.Idle))

	private fun streamAuroraReports(action: AuroraReportStreamAction): Observable<AuroraReportResult> =
		if (action.stream) {
			auroraReportModule.auroraReportFlowable
				.map<AuroraReportResult> { AuroraReportResult.Success(it) }
				.onErrorReturn { AuroraReportResult.Failure(it) }
				.toObservable()
				.startWith(AuroraReportResult.InFlight)
		} else {
			Observable.just(AuroraReportResult.Idle)
		}

	private fun showDialog(action: ShowDialogAction): DialogResult =
		DialogResult(SkylightState.Dialog(action.titleResource, action.messageResource))

	private fun hideDialog(action: HideDialogAction): DialogResult =
		DialogResult(null)

	private fun streamConnectivity(action: ConnectivityStreamAction): Observable<ConnectivityResult> =
		connectivityModule.connectivityFlowable
			.map(::ConnectivityResult)
			.toObservable()
}
