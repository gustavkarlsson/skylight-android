package se.gustavkarlsson.skylight.android.services_impl.providers

import android.net.ConnectivityManager
import io.reactivex.Single
import org.jetbrains.anko.AnkoLogger
import org.threeten.bp.Clock
import org.threeten.bp.Duration
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.extensions.now
import se.gustavkarlsson.skylight.android.services.providers.AuroraFactorsProvider
import se.gustavkarlsson.skylight.android.services.providers.AuroraReportProvider
import se.gustavkarlsson.skylight.android.services.providers.LocationNameProvider
import se.gustavkarlsson.skylight.android.services.providers.LocationProvider
import se.gustavkarlsson.skylight.android.util.UserFriendlyException
import java.util.concurrent.TimeUnit

class RealAuroraReportProvider(
	private val connectivityManager: ConnectivityManager,
	private val locationProvider: LocationProvider,
	private val auroraFactorsProvider: AuroraFactorsProvider,
	private val locationNameProvider: LocationNameProvider,
	private val clock: Clock,
	private val timeout: Duration
) : AuroraReportProvider, AnkoLogger {

	override fun get(): Single<AuroraReport> {
		return Single.fromCallable {
			checkConnectivity()
			val location = locationProvider.getLocation()
			val address = locationNameProvider.getLocationName(location)
			val auroraFactors = auroraFactorsProvider.getAuroraFactors(location)
			AuroraReport(clock.now, address.blockingGet(), auroraFactors.blockingGet())
		}.timeout(timeout.toMillis(), TimeUnit.MILLISECONDS)
	}

	private fun checkConnectivity() {
		val networkInfo = connectivityManager.activeNetworkInfo
		if (networkInfo == null || !networkInfo.isConnected) {
			throw UserFriendlyException(R.string.error_no_internet)
		}
	}
}
