package se.gustavkarlsson.skylight.android.services_impl.providers.aggregating_aurora_factors

import android.location.Location
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.debug
import org.jetbrains.anko.info
import org.threeten.bp.Instant
import se.gustavkarlsson.skylight.android.services.providers.DarknessProvider
import se.gustavkarlsson.skylight.android.entities.Darkness
import java.util.concurrent.Callable
import java.util.concurrent.TimeoutException

class GetDarkness(
	private val provider: DarknessProvider,
	private val location: Location,
	private val time: Instant
) : ErrorHandlingTask<Darkness>, AnkoLogger {

    override val callable: Callable<Darkness>
        get() = Callable { this.call() }

    private fun call(): Darkness {
        info("Getting darkness...")
        val darkness = provider.getDarkness(time, location.latitude, location.longitude)
        debug("Darkness is: $darkness")
        return darkness
    }

    override fun handleInterruptedException(e: InterruptedException) = Darkness()

    override fun handleThrowable(e: Throwable) = Darkness()

    override fun handleTimeoutException(e: TimeoutException) = Darkness()
}
