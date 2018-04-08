package se.gustavkarlsson.skylight.android.services_impl.formatters

import io.reactivex.Single
import se.gustavkarlsson.skylight.android.entities.GeomagLocation
import se.gustavkarlsson.skylight.android.services.formatters.SingleValueFormatter
import java.util.*

// TODO change locale to () -> Locale (or?)
class GeomagLocationFormatter(private val locale: Single<Locale>) : SingleValueFormatter<GeomagLocation> {
	override fun format(value: GeomagLocation): CharSequence {
		val latitude = value.latitude ?: return "?"
		return String.format(locale.blockingGet(), "%.0f°", latitude)
	}
}
