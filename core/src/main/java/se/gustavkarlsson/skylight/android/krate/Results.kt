package se.gustavkarlsson.skylight.android.krate

import se.gustavkarlsson.skylight.android.entities.Permission
import se.gustavkarlsson.skylight.android.entities.Place
import se.gustavkarlsson.skylight.android.entities.AuroraReport as AuroraReportEntity

sealed class Result {
	sealed class AuroraReport : Result() {
		data class Success(val placesToAuroraReports: Map<Place, AuroraReportEntity?>) : Result()
		data class Failure(val throwable: Throwable) : Result()
	}

	data class LocationPermission(val permission: Permission) : Result()
	data class PlaceSelected(val place: Place?) : Result()
	data class Places(val places: List<Place>) : Result()
}
