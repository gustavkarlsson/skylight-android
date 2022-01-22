package se.gustavkarlsson.skylight.android.lib.geomaglocation

import com.ioki.textref.TextRef
import dagger.Reusable
import se.gustavkarlsson.skylight.android.core.services.Formatter
import java.util.Locale
import javax.inject.Inject

@Reusable
internal class GeomagLocationFormatter @Inject constructor(
    private val locale: () -> Locale,
) : Formatter<GeomagLocation> {
    override fun format(value: GeomagLocation): TextRef {
        val latitude = value.latitude
        val text = String.format(locale(), "%.0f°", latitude)
        return TextRef.string(text)
    }
}
