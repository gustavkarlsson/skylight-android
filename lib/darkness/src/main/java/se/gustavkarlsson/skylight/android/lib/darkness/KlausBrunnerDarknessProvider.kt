package se.gustavkarlsson.skylight.android.lib.darkness

import dagger.Reusable
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import se.gustavkarlsson.skylight.android.core.logging.logInfo
import se.gustavkarlsson.skylight.android.lib.location.Location
import se.gustavkarlsson.skylight.android.lib.time.Time
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

@Reusable
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

    override fun stream(location: Location): Flow<Darkness> =
        pollDarkness(location).distinctUntilChanged()
            .distinctUntilChanged()
            .onEach { logInfo { "Streamed darkness: $it" } }

    private fun pollDarkness(location: Location) = flow {
        while (true) {
            val darknessReport = getDarkness(location, time.now())
            emit(darknessReport)
            delay(pollingInterval)
        }
    }
}
