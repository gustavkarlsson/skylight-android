package se.gustavkarlsson.skylight.android.lib.geomaglocation

import com.ioki.textref.TextRef
import me.tatarka.inject.annotations.Inject
import se.gustavkarlsson.skylight.android.core.services.Formatter
import java.util.Locale

@Inject
internal class GeomagLocationFormatter(
    private val locale: () -> Locale,
) : Formatter<GeomagLocation> {
    override fun format(value: GeomagLocation): TextRef {
        val latitude = value.latitude
        val text = String.format(locale(), "%.0f°", latitude)
        return TextRef.string(text)
    }
}
