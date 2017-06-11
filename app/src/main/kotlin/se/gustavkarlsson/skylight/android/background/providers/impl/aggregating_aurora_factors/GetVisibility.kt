package se.gustavkarlsson.skylight.android.background.providers.impl.aggregating_aurora_factors

import android.location.Location
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.debug
import org.jetbrains.anko.info
import se.gustavkarlsson.skylight.android.background.providers.VisibilityProvider
import se.gustavkarlsson.skylight.android.models.Visibility
import java.util.concurrent.Callable
import java.util.concurrent.TimeoutException

class GetVisibility(
		private val provider: VisibilityProvider,
		private val location: Location
) : ErrorHandlingTask<Visibility>, AnkoLogger {

    override val callable: Callable<Visibility>
        get() = Callable { this.call() }

    private fun call(): Visibility {
        info("Getting visibility...")
        val visibility = provider.getVisibility(location.latitude, location.longitude)
        debug("Visibility is:  $visibility")
        return visibility
    }

    override fun handleInterruptedException(e: InterruptedException) = Visibility()

    override fun handleThrowable(e: Throwable) = Visibility()

    override fun handleTimeoutException(e: TimeoutException) = Visibility()
}
