package se.gustavkarlsson.skylight.android.results

import se.gustavkarlsson.skylight.android.entities.AuroraReport

sealed class Result

sealed class AuroraReportResult : Result() {
	object InFlight : AuroraReportResult()
	data class Success(val auroraReport: AuroraReport) : AuroraReportResult()
	data class Failure(val throwable: Throwable) : AuroraReportResult()
}
