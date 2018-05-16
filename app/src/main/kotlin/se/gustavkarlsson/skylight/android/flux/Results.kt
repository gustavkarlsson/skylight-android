package se.gustavkarlsson.skylight.android.flux

import se.gustavkarlsson.skylight.android.entities.AuroraReport

sealed class Result

sealed class AuroraReportResult : Result() {
	object Idle : AuroraReportResult()
	object InFlight : AuroraReportResult()
	data class Success(val auroraReport: AuroraReport) : AuroraReportResult()
	data class Failure(val throwable: Throwable) : AuroraReportResult()
}

data class ConnectivityResult(val isConnectedToInternet: Boolean) : Result()
