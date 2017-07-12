package se.gustavkarlsson.skylight.android.entities

import android.location.Address
import org.threeten.bp.Instant

data class AuroraReport(
		val timestamp: Instant,
		val address: Address?,
		val factors: AuroraFactors
)

data class AuroraFactors(
	val geomagActivity: GeomagActivity,
	val geomagLocation: GeomagLocation,
	val darkness: Darkness,
	val visibility: Visibility
)

data class Darkness(
    val sunZenithAngle: Float? = null
)

data class GeomagActivity(
    val kpIndex: Float? = null
)

data class GeomagLocation(
    val latitude: Float? = null
)

data class Visibility(
	val cloudPercentage: Int? = null
)
