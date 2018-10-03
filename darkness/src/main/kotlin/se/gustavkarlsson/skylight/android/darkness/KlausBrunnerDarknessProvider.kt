package se.gustavkarlsson.skylight.android.darkness

import io.reactivex.Single
import io.reactivex.rxkotlin.Singles
import net.e175.klaus.solarpositioning.Grena3
import se.gustavkarlsson.koptional.Optional
import se.gustavkarlsson.skylight.android.entities.Darkness
import se.gustavkarlsson.skylight.android.entities.Location
import se.gustavkarlsson.skylight.android.entities.Report
import se.gustavkarlsson.skylight.android.extensions.toGregorianCalendar
import se.gustavkarlsson.skylight.android.services.providers.DarknessProvider
import se.gustavkarlsson.skylight.android.services.providers.Time
import timber.log.Timber

internal class KlausBrunnerDarknessProvider(
	private val time: Time
) : DarknessProvider {

	override fun get(location: Single<Optional<Location>>): Single<Report<Darkness>> {
		return Singles.zip(time.now(), location) { timeValue, maybeLocation ->
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
	}
}
