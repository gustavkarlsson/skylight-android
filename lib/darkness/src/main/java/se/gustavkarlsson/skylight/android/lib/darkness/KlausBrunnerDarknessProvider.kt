package se.gustavkarlsson.skylight.android.lib.darkness

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.datetime.Instant
import me.tatarka.inject.annotations.Inject
import net.e175.klaus.solarpositioning.Grena3
import se.gustavkarlsson.skylight.android.core.entities.Loadable
import se.gustavkarlsson.skylight.android.core.entities.Loaded
import se.gustavkarlsson.skylight.android.core.logging.logInfo
import se.gustavkarlsson.skylight.android.lib.location.Location
import se.gustavkarlsson.skylight.android.lib.time.Time
import java.util.GregorianCalendar
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

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
        val darkness = getDarkness(location, time.now())
        logInfo { "Provided darkness: $darkness" }
        return darkness
    }

    override fun stream(location: Location): Flow<Loadable<Darkness>> =
        pollDarkness(location).distinctUntilChanged()
            .distinctUntilChanged()
            .onEach { logInfo { "Streamed darkness: $it" } }

    private fun pollDarkness(location: Location) = flow {
        while (true) {
            val darknessReport = getDarkness(location, time.now())
            this.emit(Loaded(darknessReport))
            delay(pollingInterval)
        }
    }
}

private fun getDarkness(location: Location, timestamp: Instant): Darkness {
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

private fun Instant.toGregorianCalendar() = GregorianCalendar().apply { timeInMillis = toEpochMilliseconds() }
