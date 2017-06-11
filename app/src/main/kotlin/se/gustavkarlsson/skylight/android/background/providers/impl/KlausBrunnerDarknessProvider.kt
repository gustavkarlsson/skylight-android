package se.gustavkarlsson.skylight.android.background.providers.impl

import dagger.Reusable
import net.e175.klaus.solarpositioning.Grena3
import org.threeten.bp.Instant
import se.gustavkarlsson.skylight.android.background.providers.DarknessProvider
import se.gustavkarlsson.skylight.android.models.Darkness
import java.util.*
import javax.inject.Inject

@Reusable
class KlausBrunnerDarknessProvider
@Inject
constructor() : DarknessProvider {

    override fun getDarkness(time: Instant, latitude: Double, longitude: Double): Darkness {
        val date = GregorianCalendar().apply { timeInMillis = time.toEpochMilli() }
        val azimuthZenithAngle = Grena3.calculateSolarPosition(date, latitude, longitude, 0.0)
        return Darkness(azimuthZenithAngle.zenithAngle.toFloat())
    }
}
