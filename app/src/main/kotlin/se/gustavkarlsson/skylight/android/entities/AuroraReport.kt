package se.gustavkarlsson.skylight.android.entities

import org.threeten.bp.Instant

data class AuroraReport(
	val timestamp: Instant,
	val locationName: String?,
	val factors: AuroraFactors
) {
	companion object {
	    val default = AuroraReport(Instant.EPOCH, null, AuroraFactors(
			KpIndex(),
			GeomagLocation(),
			Darkness(),
			Visibility()
		))
	}
}

data class AuroraFactors(
	val kpIndex: KpIndex,
	val geomagLocation: GeomagLocation,
	val darkness: Darkness,
	val visibility: Visibility
)

data class Darkness(
    val sunZenithAngle: Double? = null
)

data class KpIndex(
    val value: Double? = null
)

data class GeomagLocation(
    val latitude: Double? = null
)

data class Visibility(
	val cloudPercentage: Int? = null
)
