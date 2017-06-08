package se.gustavkarlsson.skylight.android.background.providers.impl.aggregating_aurora_factors

import android.location.Location
import android.util.Log
import org.threeten.bp.Instant
import se.gustavkarlsson.skylight.android.background.providers.DarknessProvider
import se.gustavkarlsson.skylight.android.models.factors.Darkness
import java.util.concurrent.Callable
import java.util.concurrent.TimeoutException

class GetDarkness(
		private val provider: DarknessProvider,
		private val location: Location,
		private val time: Instant
) : ErrorHandlingTask<Darkness> {

    override val callable: Callable<Darkness>
        get() = Callable { this.call() }

    private fun call(): Darkness {
        Log.i(TAG, "Getting darkness...")
        val darkness = provider.getDarkness(time, location.latitude, location.longitude)
        Log.d(TAG, "Darkness is: " + darkness)
        return darkness
    }

    override fun handleInterruptedException(e: InterruptedException): Darkness {
        return Darkness()
    }

    override fun handleThrowable(e: Throwable): Darkness {
        return Darkness()
    }

    override fun handleTimeoutException(e: TimeoutException): Darkness {
        return Darkness()
    }

    companion object {
        private val TAG = GetDarkness::class.java.simpleName
    }
}
