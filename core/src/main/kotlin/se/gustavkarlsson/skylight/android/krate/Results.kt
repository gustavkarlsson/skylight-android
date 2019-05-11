package se.gustavkarlsson.skylight.android.krate

import se.gustavkarlsson.skylight.android.entities.AuroraReport as AuroraReportEntity

sealed class Result {
	sealed class AuroraReport : Result() {
		data class Success(val placeIdsToAuroraReports: Map<Int, AuroraReportEntity?>) : Result()
		data class Failure(val throwable: Throwable) : Result()
	}

	data class LocationPermission(val isGranted: Boolean) : Result()
	data class GooglePlayServices(val isAvailable: Boolean) : Result()
	data class FirstRun(val isFirstRun: Boolean) : Result()
	data class PlaceSelected(val placeId: Int?) : Result()
}
