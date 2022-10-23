package se.gustavkarlsson.skylight.android.lib.darkness

import kotlinx.datetime.Instant
import net.e175.klaus.solarpositioning.Grena3
import se.gustavkarlsson.skylight.android.lib.location.Location
import java.time.ZoneOffset
import java.util.GregorianCalendar
import java.util.TimeZone

internal fun getDarkness(location: Location, timestamp: Instant): Darkness {
    val sunZenithAngle = calculateSunZenithAngle(location, timestamp)
    return Darkness(sunZenithAngle, timestamp)
}

private fun calculateSunZenithAngle(location: Location, time: Instant): Double {
    val date = time.toGregorianCalendar()
    val azimuthAndZenithAngle = Grena3.calculateSolarPosition(
        date,
        location.latitude,
        location.longitude,
        0.0,
    )
    return azimuthAndZenithAngle.zenithAngle
}

private fun Instant.toGregorianCalendar() = GregorianCalendar(TimeZone.getTimeZone(ZoneOffset.UTC))
    .apply { timeInMillis = toEpochMilliseconds() }
