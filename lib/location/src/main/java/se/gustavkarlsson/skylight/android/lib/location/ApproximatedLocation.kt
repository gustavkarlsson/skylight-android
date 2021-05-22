package se.gustavkarlsson.skylight.android.lib.location

import kotlin.math.roundToInt

class ApproximatedLocation(exactLocation: Location, approximationMeters: Double) {
    val latitude: Double = approximate(exactLocation.latitude, approximationMeters)
    val longitude: Double = approximate(exactLocation.longitude, approximationMeters)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ApproximatedLocation

        if (latitude != other.latitude) return false
        if (longitude != other.longitude) return false

        return true
    }

    override fun hashCode(): Int {
        var result = latitude.hashCode()
        result = 31 * result + longitude.hashCode()
        return result
    }

    override fun toString(): String {
        return "ApproximatedLocation(latitude=$latitude, longitude=$longitude)"
    }
}

fun Location.approximate(distanceMeters: Double): ApproximatedLocation {
    return ApproximatedLocation(this, distanceMeters)
}

private const val METERS_PER_DEGREE = 111_111.0

private fun approximate(degrees: Double, approximationMeters: Double): Double {
    val meters = degrees * METERS_PER_DEGREE
    val approxMeters = (meters / approximationMeters).roundToInt() * approximationMeters
    return approxMeters / METERS_PER_DEGREE
}
