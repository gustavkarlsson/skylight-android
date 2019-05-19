package se.gustavkarlsson.skylight.android.services_impl.formatters

import com.ioki.textref.TextRef
import io.reactivex.Single
import se.gustavkarlsson.skylight.android.entities.GeomagLocation
import se.gustavkarlsson.skylight.android.formatters.SingleValueFormatter
import java.util.Locale

class GeomagLocationFormatter(private val locale: Single<Locale>) : SingleValueFormatter<GeomagLocation> {
	override fun format(value: GeomagLocation): TextRef {
		val latitude = value.latitude
		val text = String.format(locale.blockingGet(), "%.0f°", latitude)
		return TextRef(text)
	}
}
