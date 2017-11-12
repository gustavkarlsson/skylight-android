package se.gustavkarlsson.skylight.android.services_impl.providers

import dagger.Reusable
import io.reactivex.Single
import net.e175.klaus.solarpositioning.Grena3
import org.threeten.bp.Instant
import se.gustavkarlsson.skylight.android.entities.Darkness
import se.gustavkarlsson.skylight.android.services.Location
import se.gustavkarlsson.skylight.android.services.providers.DarknessProvider
import java.util.*
import javax.inject.Inject

@Reusable
class KlausBrunnerDarknessProvider
@Inject
constructor() : DarknessProvider {

	override fun getDarkness(time: Single<Instant>, location: Single<Location>): Single<Darkness> {
		return Single.fromCallable {
			val date = GregorianCalendar().apply { timeInMillis = time.blockingGet().toEpochMilli() }
			val azimuthZenithAngle = Grena3.calculateSolarPosition(date, location.blockingGet().latitude, location.blockingGet().longitude, 0.0)
			Darkness(azimuthZenithAngle.zenithAngle)
		}
	}
}
