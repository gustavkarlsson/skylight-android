package se.gustavkarlsson.skylight.android.services_impl.providers

import com.hadisatrio.optional.Optional
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import net.e175.klaus.solarpositioning.Grena3
import org.threeten.bp.Instant
import se.gustavkarlsson.skylight.android.entities.Darkness
import se.gustavkarlsson.skylight.android.entities.Location
import se.gustavkarlsson.skylight.android.extensions.toGregorianCalendar
import se.gustavkarlsson.skylight.android.services.providers.DarknessProvider
import timber.log.Timber

class KlausBrunnerDarknessProvider : DarknessProvider {

	override fun get(time: Single<Instant>, location: Single<Optional<Location>>): Single<Darkness> {
		return Single.zip(time, location,
			BiFunction<Instant, Optional<Location>, Darkness> { timeValue, maybeLocation ->
				maybeLocation.orNull()?.let {
					val date = timeValue.toGregorianCalendar()
					val azimuthZenithAngle = Grena3.calculateSolarPosition(
						date,
						it.latitude,
						it.longitude,
						0.0
					)
					Darkness(azimuthZenithAngle.zenithAngle)
				} ?: Darkness()
			})
			.onErrorReturnItem(Darkness())
			.doOnSuccess { Timber.i("Provided darkness: %s", it) }
	}
}
