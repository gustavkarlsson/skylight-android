package se.gustavkarlsson.skylight.android.lib.darkness

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import me.tatarka.inject.annotations.Inject
import se.gustavkarlsson.skylight.android.core.logging.logInfo
import se.gustavkarlsson.skylight.android.lib.location.Location
import se.gustavkarlsson.skylight.android.lib.time.Time
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
