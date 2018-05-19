package se.gustavkarlsson.skylight.android.di.modules

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import se.gustavkarlsson.skylight.android.flux.*

class RealFluxModule(
	private val auroraReportModule: AuroraReportModule,
	private val connectivityModule: ConnectivityModule
) : FluxModule {
	override val store: SkylightStore by lazy {
		Store.Builder<SkylightState, SkylightAction, SkylightResult>(SkylightState(), reducer)
			.switchMapAction(::getAuroraReport)
			.switchMapAction(::streamAuroraReports)
			.switchMapAction(::streamConnectivity)
			.mapAction(::showDialog)
			.mapAction(::hideDialog)
			.setObserveScheduler(AndroidSchedulers.mainThread())
			.build()
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

	private val reducer: (current: SkylightState, result: SkylightResult) -> SkylightState by lazy {
		{ current: SkylightState, result: SkylightResult ->
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
				is DialogResult -> current.copy(
					dialog = result.dialog
				)
			}
		}
	}
}
