package se.gustavkarlsson.skylight.android.background.providers.impl

import android.location.Address
import android.net.ConnectivityManager
import android.util.Log
import org.threeten.bp.Clock
import org.threeten.bp.Duration
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.background.providers.AsyncAddressProvider
import se.gustavkarlsson.skylight.android.background.providers.AuroraFactorsProvider
import se.gustavkarlsson.skylight.android.background.providers.AuroraReportProvider
import se.gustavkarlsson.skylight.android.background.providers.LocationProvider
import se.gustavkarlsson.skylight.android.models.AuroraReport
import se.gustavkarlsson.skylight.android.util.CountdownTimer
import se.gustavkarlsson.skylight.android.util.UserFriendlyException
import java.util.concurrent.CancellationException
import java.util.concurrent.ExecutionException
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit.MILLISECONDS
import java.util.concurrent.TimeoutException

class AuroraReportProviderImpl(
		private val connectivityManager: ConnectivityManager,
		private val locationProvider: LocationProvider,
		private val auroraFactorsProvider: AuroraFactorsProvider,
		private val asyncAddressProvider: AsyncAddressProvider,
		private val clock: Clock
) : AuroraReportProvider {

    override fun getReport(timeout: Duration): AuroraReport {
        val networkInfo = connectivityManager.activeNetworkInfo
        if (networkInfo == null || !networkInfo.isConnected) {
            throw UserFriendlyException(R.string.error_no_internet)
        }

        val timeoutTimer = CountdownTimer(timeout, clock)
        val location = locationProvider.getLocation(timeoutTimer.remainingTime)
        val addressFuture = asyncAddressProvider.execute(location.latitude, location.longitude)
        val auroraFactors = auroraFactorsProvider.getAuroraFactors(location, timeoutTimer.remainingTime)
        val address = getAddress(addressFuture, timeoutTimer.remainingTime)
        return AuroraReport(clock.millis(), address, auroraFactors)
    }

	private fun getAddress(addressFuture: Future<Address?>, timeout: Duration): Address? {
		try {
			return addressFuture.get(timeout.toMillis(), MILLISECONDS)
		} catch (e: TimeoutException) {
			Log.w(TAG, "Getting address timed out after " + timeout.toMillis() + "ms", e)
		} catch (e: ExecutionException) {
			val cause = e.cause
			Log.w(TAG, "An unexpected exception occurred while", cause)
		} catch (e: InterruptedException) {
			Log.w(TAG, "An unexpected exception occurred", e)
		} catch (e: CancellationException) {
			Log.w(TAG, "An unexpected exception occurred", e)
		}

		return null
	}

    companion object {
        private val TAG = AuroraReportProviderImpl::class.java.simpleName
    }
}
