package se.gustavkarlsson.skylight.android.lib.darkness

import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.rxkotlin.Singles
import net.e175.klaus.solarpositioning.Grena3
import org.threeten.bp.Duration
import se.gustavkarlsson.koptional.Optional
import se.gustavkarlsson.skylight.android.entities.Darkness
import se.gustavkarlsson.skylight.android.entities.Location
import se.gustavkarlsson.skylight.android.entities.Report
import se.gustavkarlsson.skylight.android.extensions.delay
import se.gustavkarlsson.skylight.android.extensions.toGregorianCalendar
import se.gustavkarlsson.skylight.android.lib.darkness.R
import se.gustavkarlsson.skylight.android.services.providers.DarknessProvider
import se.gustavkarlsson.skylight.android.services.providers.Time
import timber.log.Timber

internal class KlausBrunnerDarknessProvider(
	private val time: Time,
	private val pollingInterval: Duration
) : DarknessProvider {

	override fun get(location: Single<Optional<Location>>): Single<Report<Darkness>> =
		Singles
			.zip(time.now(), location) { timeValue, maybeLocation ->
				maybeLocation.value?.let {
					val date = timeValue.toGregorianCalendar()
					val azimuthZenithAngle = Grena3.calculateSolarPosition(
						date,
						it.latitude,
						it.longitude,
						0.0
					)
					Report.success(Darkness(azimuthZenithAngle.zenithAngle), timeValue)
				} ?: Report.error(R.string.error_no_location, timeValue)
			}
			.doOnSuccess { Timber.i("Provided darkness: %s", it) }

	override fun stream(locations: Flowable<Optional<Location>>): Flowable<Report<Darkness>> =
		locations
			.switchMap { location ->
				get(Single.just(location))
					.repeatWhen { it.delay(pollingInterval) }
			}
			.distinctUntilChanged()
			.doOnNext { Timber.i("Streamed darkness: %s", it) }
			.replay(1)
			.refCount()
}
