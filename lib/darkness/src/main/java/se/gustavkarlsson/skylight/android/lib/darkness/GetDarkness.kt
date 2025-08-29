package se.gustavkarlsson.skylight.android.lib.darkness

import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaZoneId
import net.e175.klaus.solarpositioning.Grena3
import se.gustavkarlsson.skylight.android.lib.location.Location
import java.time.ZonedDateTime
import kotlin.time.Instant
import kotlin.time.toJavaInstant

internal fun getDarkness(location: Location, timestamp: Instant, timeZone: TimeZone): Darkness {
    val sunZenithAngle = calculateSunZenithAngle(location, timestamp.toJavaInstant().atZone(timeZone.toJavaZoneId()))
    return Darkness(sunZenithAngle, timestamp)
}

private fun calculateSunZenithAngle(location: Location, date: ZonedDateTime): Double {
    val azimuthAndZenithAngle = Grena3.calculateSolarPosition(
        date,
        location.latitude,
        location.longitude,
        0.0,
    )
    return azimuthAndZenithAngle.zenithAngle
}
