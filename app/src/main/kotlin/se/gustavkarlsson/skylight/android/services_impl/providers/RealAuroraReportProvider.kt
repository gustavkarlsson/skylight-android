package se.gustavkarlsson.skylight.android.services_impl.providers

import android.net.ConnectivityManager
import kotlinx.coroutines.experimental.runBlocking
import kotlinx.coroutines.experimental.withTimeoutOrNull
import org.jetbrains.anko.AnkoLogger
import org.threeten.bp.Clock
import org.threeten.bp.Duration
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.extensions.now
import se.gustavkarlsson.skylight.android.services.SingletonCache
import se.gustavkarlsson.skylight.android.services.providers.AddressProvider
import se.gustavkarlsson.skylight.android.services.providers.AuroraFactorsProvider
import se.gustavkarlsson.skylight.android.services.providers.AuroraReportProvider
import se.gustavkarlsson.skylight.android.services.providers.LocationProvider
import se.gustavkarlsson.skylight.android.util.UserFriendlyException
import java.util.concurrent.TimeUnit

class RealAuroraReportProvider(
        private val cache: SingletonCache<AuroraReport>,
        private val connectivityManager: ConnectivityManager,
        private val locationProvider: LocationProvider,
        private val auroraFactorsProvider: AuroraFactorsProvider,
        private val addressProvider: AddressProvider,
        private val clock: Clock,
        private val timeout: Duration
) : AuroraReportProvider, AnkoLogger {

    override fun get(): AuroraReport {
        val networkInfo = connectivityManager.activeNetworkInfo
        if (networkInfo == null || !networkInfo.isConnected) {
            throw UserFriendlyException(R.string.error_no_internet)
        }
        val auroraReport = runBlocking {
            withTimeoutOrNull(timeout.toMillis(), TimeUnit.MILLISECONDS) {
                val location = locationProvider.getLocation()
                val address = addressProvider.getAddress(location.latitude, location.longitude)
                val auroraFactors = auroraFactorsProvider.getAuroraFactors(location)
                AuroraReport(clock.now, address?.locality, auroraFactors)
            }
        } ?: throw UserFriendlyException(R.string.error_updating_took_too_long)
        cache.value = auroraReport
        return auroraReport
    }
}
