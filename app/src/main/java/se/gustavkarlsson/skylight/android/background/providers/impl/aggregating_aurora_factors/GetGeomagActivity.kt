package se.gustavkarlsson.skylight.android.background.providers.impl.aggregating_aurora_factors

import android.util.Log
import se.gustavkarlsson.skylight.android.background.providers.GeomagActivityProvider
import se.gustavkarlsson.skylight.android.models.factors.GeomagActivity
import java.util.concurrent.Callable
import java.util.concurrent.TimeoutException

internal class GetGeomagActivity(
		private val provider: GeomagActivityProvider) : ErrorHandlingTask<GeomagActivity>
{

    override val callable: Callable<GeomagActivity>
        get() = Callable { this.call() }

    private fun call(): GeomagActivity {
        Log.i(TAG, "Getting geomagnetic activity...")
        val geomagActivity = provider.geomagActivity
        Log.d(TAG, "Geomagnetic activity is: " + geomagActivity)
        return geomagActivity
    }

    override fun handleInterruptedException(e: InterruptedException): GeomagActivity {
        return GeomagActivity()
    }

    override fun handleThrowable(e: Throwable): GeomagActivity {
        return GeomagActivity()
    }

    override fun handleTimeoutException(e: TimeoutException): GeomagActivity {
        return GeomagActivity()
    }

    companion object {
        private val TAG = GetGeomagActivity::class.java.simpleName
    }
}
