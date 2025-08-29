package se.gustavkarlsson.skylight.android.lib.darkness

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaZoneId
import me.tatarka.inject.annotations.Inject
import net.e175.klaus.solarpositioning.Grena3
import se.gustavkarlsson.skylight.android.core.logging.logInfo
import se.gustavkarlsson.skylight.android.lib.location.Location
import se.gustavkarlsson.skylight.android.lib.time.Time
import java.time.ZonedDateTime
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Instant
import kotlin.time.toJavaInstant

internal class KlausBrunnerDarknessProvider(
    private val time: Time,
    private val pollingInterval: Duration,
) : DarknessProvider {

    @Inject
    constructor(
        time: Time,
    ) : this(
        time = time,
        pollingInterval = 1.minutes,
    )

    override fun get(location: Location): Darkness {
        val darkness = getDarkness(location, time.now(), time.timeZone())
        logInfo { "Provided darkness: $darkness" }
        return darkness
    }

    override fun stream(location: Location): Flow<Darkness> =
        pollDarkness(location).distinctUntilChanged()
            .distinctUntilChanged()
            .onEach { logInfo { "Streamed darkness: $it" } }

    private fun pollDarkness(location: Location) = flow {
        while (true) {
            val darknessReport = getDarkness(location, time.now(), time.timeZone())
            emit(darknessReport)
            delay(pollingInterval)
        }
    }
}

private fun getDarkness(location: Location, timestamp: Instant, timeZone: TimeZone): Darkness {
    val sunZenithAngle = calculateSunZenithAngle(location, timestamp.toJavaInstant().atZone(timeZone.toJavaZoneId()))
    return Darkness(sunZenithAngle, timestamp)
}

private fun calculateSunZenithAngle(location: Location, time: ZonedDateTime): Double {
    val azimuthAndZenithAngle = Grena3.calculateSolarPosition(
        time,
        location.latitude,
        location.longitude,
        0.0,
    )
    return azimuthAndZenithAngle.zenithAngle
}
