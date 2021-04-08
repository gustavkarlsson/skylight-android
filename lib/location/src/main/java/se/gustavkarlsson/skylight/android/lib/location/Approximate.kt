package se.gustavkarlsson.skylight.android.lib.location

import kotlin.math.roundToInt

private const val METERS_PER_DEGREE = 111_111.0

fun Location.approximate(distanceMeters: Double): Location {
    val newLatitude = approximate(latitude, distanceMeters)
    val newLongitude = approximate(longitude, distanceMeters)
    return Location(newLatitude, newLongitude)
}

private fun approximate(degrees: Double, approximationMeters: Double): Double {
    val meters = degrees * METERS_PER_DEGREE
    val approxMeters = (meters / approximationMeters).roundToInt() * approximationMeters
    return approxMeters / METERS_PER_DEGREE
}
