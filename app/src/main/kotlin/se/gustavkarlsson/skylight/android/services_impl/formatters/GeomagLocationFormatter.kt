package se.gustavkarlsson.skylight.android.services_impl.formatters

import se.gustavkarlsson.skylight.android.entities.GeomagLocation
import se.gustavkarlsson.skylight.android.services.formatters.SingleValueFormatter
import java.util.*

class GeomagLocationFormatter(private val locale: Locale) : SingleValueFormatter<GeomagLocation> {
	override fun format(value: GeomagLocation): CharSequence {
		val latitude = value.latitude ?: return "?"
		return String.format(locale, "%.0f°", latitude)
	}
}
