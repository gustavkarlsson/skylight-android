package se.gustavkarlsson.skylight.android.services_impl.providers

import android.net.ConnectivityManager
import com.hadisatrio.optional.Optional
import io.reactivex.Single
import io.reactivex.functions.Function3
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.AnkoLogger
import org.threeten.bp.Instant
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.entities.AuroraFactors
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.services.providers.*
import se.gustavkarlsson.skylight.android.util.UserFriendlyException

class RealAuroraReportProvider(
	private val connectivityManager: ConnectivityManager,
	private val locationProvider: LocationProvider,
	private val auroraFactorsProvider: AuroraFactorsProvider,
	private val locationNameProvider: LocationNameProvider,
	private val timeProvider: TimeProvider
) : AuroraReportProvider, AnkoLogger {

	override fun get(): Single<AuroraReport> {
		return Single.fromCallable {
			checkConnectivity()
			val location = locationProvider.get().cache()
			val time = timeProvider.getTime().cache()
			Single.zip(
				time,
				locationNameProvider.get(location),
				auroraFactorsProvider.get(time, location),
				Function3<Instant, Optional<String>, AuroraFactors, AuroraReport>
				{ theTime, locationName, auroraFactors ->
					AuroraReport(theTime, locationName.orNull(), auroraFactors)
				})
		}
			.subscribeOn(Schedulers.computation())
			.flatMap { it }
	}

	private fun checkConnectivity() { // TODO Should we check connectivity here?
		val networkInfo = connectivityManager.activeNetworkInfo
		if (networkInfo == null || !networkInfo.isConnected) {
			throw UserFriendlyException(R.string.error_no_internet)
		}
	}
}
