package se.gustavkarlsson.skylight.android.krate

import se.gustavkarlsson.skylight.android.entities.AuroraReport

sealed class SkylightResult

sealed class AuroraReportResult : SkylightResult() {
	object Idle : AuroraReportResult()
	object InFlight : AuroraReportResult()
	object JustFinished : AuroraReportResult()
	data class Success(val auroraReport: AuroraReport) : AuroraReportResult()
	data class Failure(val throwable: Throwable) : AuroraReportResult()
}

data class SettingsResult(val settings: SkylightState.Settings) : SkylightResult()

data class LocationPermissionResult(val isGranted: Boolean) : SkylightResult()

data class GooglePlayServicesResult(val isAvailable: Boolean) : SkylightResult()

data class FirstRunResult(val isFirstRun: Boolean) : SkylightResult()
