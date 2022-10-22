package se.gustavkarlsson.skylight.android.lib.darkness

import dagger.Reusable
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.datetime.Instant
import se.gustavkarlsson.skylight.android.core.entities.Loadable
import se.gustavkarlsson.skylight.android.core.entities.Loaded
import se.gustavkarlsson.skylight.android.core.logging.logInfo
import se.gustavkarlsson.skylight.android.lib.location.Location
import se.gustavkarlsson.skylight.android.lib.time.Time
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes

@Reusable
internal class KlausBrunnerDarknessForecastProvider(
    private val time: Time,
    private val pollingInterval: Duration,
) : DarknessForecastProvider {

    @Inject
    constructor(
        time: Time,
    ) : this(
        time = time,
        pollingInterval = 10.minutes,
    )

    override fun get(location: Location): DarknessForecast {
        val darknesses = generateDarknesses(time.now(), location)
        val forecast = DarknessForecast(darknesses.toList())
        logInfo { "Provided darkness forecast: $forecast" }
        return forecast
    }

    private fun generateDarknesses(start: Instant, location: Location): Sequence<Darkness> {
        return generateSequence(start) { lastTimestamp ->
            val timestamp = lastTimestamp + 30.minutes
            if (timestamp > start + 7.days) {
                null
            } else {
                timestamp
            }
        }.map { timestamp ->
            getDarkness(location, timestamp)
        }
    }

    override fun stream(location: Location): Flow<Loadable<DarknessForecast>> =
        pollDarkness(location).distinctUntilChanged()
            .distinctUntilChanged()
            .onEach { logInfo { "Streamed darkness forecast: $it" } }

    private fun pollDarkness(location: Location) = flow {
        while (true) {
            val darknesses = generateDarknesses(time.now(), location)
            val forecast = DarknessForecast(darknesses.toList())
            this.emit(Loaded(forecast))
            delay(pollingInterval)
        }
    }
}
