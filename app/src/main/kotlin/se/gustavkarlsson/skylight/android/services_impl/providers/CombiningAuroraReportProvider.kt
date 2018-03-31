package se.gustavkarlsson.skylight.android.services_impl.providers

import com.hadisatrio.optional.Optional
import io.reactivex.Single
import io.reactivex.functions.Function3
import io.reactivex.schedulers.Schedulers
import org.threeten.bp.Instant
import se.gustavkarlsson.skylight.android.entities.AuroraFactors
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.services.providers.*
import timber.log.Timber

class CombiningAuroraReportProvider(
	private val locationProvider: LocationProvider,
	private val auroraFactorsProvider: AuroraFactorsProvider,
	private val locationNameProvider: LocationNameProvider,
	private val timeProvider: TimeProvider
) : AuroraReportProvider {

	override fun get(): Single<AuroraReport> {
		return Single.fromCallable {
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
			.doOnSuccess { Timber.i("Provided aurora report: %s", it) }
	}
}
