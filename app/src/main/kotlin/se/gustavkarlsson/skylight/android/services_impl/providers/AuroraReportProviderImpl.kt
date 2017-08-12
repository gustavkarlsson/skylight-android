package se.gustavkarlsson.skylight.android.services_impl.providers

import android.location.Address
import android.net.ConnectivityManager
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.warn
import org.threeten.bp.Clock
import org.threeten.bp.Duration
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.extensions.now
import se.gustavkarlsson.skylight.android.services.providers.AsyncAddressProvider
import se.gustavkarlsson.skylight.android.services.providers.AuroraFactorsProvider
import se.gustavkarlsson.skylight.android.services.providers.AuroraReportProvider
import se.gustavkarlsson.skylight.android.services.providers.LocationProvider
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
	private val clock: Clock,
	private val timeout: Duration
) : AuroraReportProvider, AnkoLogger {

    override fun get(): AuroraReport {
		val timeoutTimer = CountdownTimer(timeout, clock)
        val networkInfo = connectivityManager.activeNetworkInfo
        if (networkInfo == null || !networkInfo.isConnected) {
            throw UserFriendlyException(R.string.error_no_internet)
        }
        val location = locationProvider.getLocation(timeoutTimer.remainingTime)
        val addressFuture = asyncAddressProvider.execute(location.latitude, location.longitude)
        val auroraFactors = auroraFactorsProvider.getAuroraFactors(location, timeoutTimer.remainingTime)
        val address = getAddress(addressFuture, timeoutTimer.remainingTime)
        return AuroraReport(clock.now, address?.locality, auroraFactors)
    }

	private fun getAddress(addressFuture: Future<Address?>, timeout: Duration): Address? {
		try {
			return addressFuture.get(timeout.toMillis(), MILLISECONDS)
		} catch (e: TimeoutException) {
			warn("Getting address timed out after ${timeout.toMillis()}ms", e)
		} catch (e: ExecutionException) {
			warn("An unexpected exception occurred while getting address", e.cause)
		} catch (e: InterruptedException) {
			warn("An unexpected exception occurred while getting address", e)
		} catch (e: CancellationException) {
			warn("An unexpected exception occurred while getting address", e)
		}

		return null
	}
}
