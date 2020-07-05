package se.gustavkarlsson.skylight.android.lib.geomaglocation

import com.ioki.textref.TextRef
import io.reactivex.Single
import java.util.Locale
import se.gustavkarlsson.skylight.android.services.Formatter

internal class GeomagLocationFormatter(private val locale: Single<Locale>) :
    Formatter<GeomagLocation> {
    override fun format(value: GeomagLocation): TextRef {
        val latitude = value.latitude
        val text = String.format(locale.blockingGet(), "%.0f°", latitude)
        return TextRef(text)
    }
}
