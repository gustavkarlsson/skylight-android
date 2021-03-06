package se.gustavkarlsson.skylight.android.lib.geomaglocation

import com.ioki.textref.TextRef
import se.gustavkarlsson.skylight.android.core.services.Formatter
import java.util.Locale

internal class GeomagLocationFormatter(private val locale: () -> Locale) : Formatter<GeomagLocation> {
    override fun format(value: GeomagLocation): TextRef {
        val latitude = value.latitude
        val text = String.format(locale(), "%.0f°", latitude)
        return TextRef.string(text)
    }
}
