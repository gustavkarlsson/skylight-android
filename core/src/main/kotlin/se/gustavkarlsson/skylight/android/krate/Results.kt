package se.gustavkarlsson.skylight.android.krate

import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.entities.Place

sealed class SkylightResult

sealed class AuroraReportResult : SkylightResult() {
	data class Success(val auroraReport: AuroraReport) : AuroraReportResult()
	data class Failure(val throwable: Throwable) : AuroraReportResult()
}

data class SettingsResult(val settings: SkylightState.Settings) : SkylightResult()

data class LocationPermissionResult(val isGranted: Boolean) : SkylightResult()

data class GooglePlayServicesResult(val isAvailable: Boolean) : SkylightResult()

data class FirstRunResult(val isFirstRun: Boolean) : SkylightResult()

data class PlaceSelectedResult(val place: Place?) : SkylightResult()
