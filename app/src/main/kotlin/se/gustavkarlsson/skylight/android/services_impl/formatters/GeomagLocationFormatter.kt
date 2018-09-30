package se.gustavkarlsson.skylight.android.services_impl.formatters

import io.reactivex.Single
import se.gustavkarlsson.skylight.android.entities.GeomagLocation
import se.gustavkarlsson.skylight.android.services.formatters.SingleValueFormatter
import java.util.Locale

class GeomagLocationFormatter(private val locale: Single<Locale>) : SingleValueFormatter<GeomagLocation> {
	override fun format(value: GeomagLocation): CharSequence {
		val latitude = value.latitude
		return String.format(locale.blockingGet(), "%.0f°", latitude)
	}
}
