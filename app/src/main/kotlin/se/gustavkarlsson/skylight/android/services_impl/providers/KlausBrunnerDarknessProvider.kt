package se.gustavkarlsson.skylight.android.services_impl.providers

import dagger.Reusable
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import net.e175.klaus.solarpositioning.Grena3
import org.threeten.bp.Instant
import se.gustavkarlsson.skylight.android.entities.Darkness
import se.gustavkarlsson.skylight.android.entities.Location
import se.gustavkarlsson.skylight.android.extensions.toGregorianCalendar
import se.gustavkarlsson.skylight.android.services.providers.DarknessProvider
import timber.log.Timber
import javax.inject.Inject

@Reusable
class KlausBrunnerDarknessProvider
@Inject
constructor() : DarknessProvider {

	override fun get(time: Single<Instant>, location: Single<Location>): Single<Darkness> {
		return Single.zip(time, location,
			BiFunction<Instant, Location, Darkness> { timeValue, locationValue ->
				val date = timeValue.toGregorianCalendar()
				val azimuthZenithAngle = Grena3.calculateSolarPosition(
					date,
					locationValue.latitude,
					locationValue.longitude,
					0.0
				)
				Darkness(azimuthZenithAngle.zenithAngle)
			})
			.onErrorReturnItem(Darkness())
			.doOnSuccess { Timber.i("Provided darkness: %s", it) }
	}
}
