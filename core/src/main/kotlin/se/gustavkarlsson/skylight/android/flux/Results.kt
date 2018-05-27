package se.gustavkarlsson.skylight.android.flux

import se.gustavkarlsson.skylight.android.entities.AuroraReport

sealed class SkylightResult

sealed class AuroraReportResult : SkylightResult() {
	object Idle : AuroraReportResult()
	object InFlight : AuroraReportResult()
	object JustFinished : AuroraReportResult()
	data class Success(val auroraReport: AuroraReport) : AuroraReportResult()
	data class Failure(val throwable: Throwable) : AuroraReportResult()
}

data class ConnectivityResult(val isConnectedToInternet: Boolean) : SkylightResult()

data class DialogResult(val dialog: SkylightState.Dialog?) : SkylightResult()

object LocationPermissionGrantedResult : SkylightResult()
