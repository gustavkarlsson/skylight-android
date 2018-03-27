package se.gustavkarlsson.skylight.android.services_impl.providers

import dagger.Reusable
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import net.e175.klaus.solarpositioning.Grena3
import org.threeten.bp.Instant
import se.gustavkarlsson.skylight.android.entities.Darkness
import se.gustavkarlsson.skylight.android.entities.Location
import se.gustavkarlsson.skylight.android.services.providers.DarknessProvider
import java.util.*
import javax.inject.Inject

@Reusable
class KlausBrunnerDarknessProvider
@Inject
constructor() : DarknessProvider {

	override fun get(time: Single<Instant>, location: Single<Location>): Single<Darkness> {
		return Single.zip(time, location,
			BiFunction<Instant, Location, Darkness> { timeValue, locationValue ->
				val date = GregorianCalendar().apply { timeInMillis = timeValue.toEpochMilli() }
				val azimuthZenithAngle = Grena3.calculateSolarPosition(date, locationValue.latitude, locationValue.longitude, 0.0)
				Darkness(azimuthZenithAngle.zenithAngle)
			})
	}
}
