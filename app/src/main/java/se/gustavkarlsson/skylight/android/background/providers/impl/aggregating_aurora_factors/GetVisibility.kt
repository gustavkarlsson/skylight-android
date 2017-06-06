package se.gustavkarlsson.skylight.android.background.providers.impl.aggregating_aurora_factors

import android.location.Location
import android.util.Log
import se.gustavkarlsson.skylight.android.background.providers.VisibilityProvider
import se.gustavkarlsson.skylight.android.models.factors.Visibility
import java.util.concurrent.Callable
import java.util.concurrent.TimeoutException

internal class GetVisibility(
		private val provider: VisibilityProvider,
		private val location: Location) : ErrorHandlingTask<Visibility>
{

    override val callable: Callable<Visibility>
        get() = Callable { this.call() }

    private fun call(): Visibility {
        Log.i(TAG, "Getting visibility...")
        val visibility = provider.getVisibility(location.latitude, location.longitude)
        Log.d(TAG, "Visibility is:  " + visibility)
        return visibility
    }

    override fun handleInterruptedException(e: InterruptedException): Visibility {
        return Visibility()
    }

    override fun handleThrowable(e: Throwable): Visibility {
        return Visibility()
    }

    override fun handleTimeoutException(e: TimeoutException): Visibility {
        return Visibility()
    }

    companion object {
        private val TAG = GetVisibility::class.java.simpleName
    }
}
