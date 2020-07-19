package se.gustavkarlsson.skylight.android.lib.weather

import com.ioki.textref.TextRef
import se.gustavkarlsson.skylight.android.core.services.Formatter

internal object WeatherFormatter : Formatter<Weather> {
    override fun format(value: Weather): TextRef {
        val clouds = value.cloudPercentage
        return when {
            clouds < 10 -> TextRef.stringRes(R.string.weather_clear)
            clouds < 30 -> TextRef.stringRes(R.string.weather_some_clouds)
            clouds < 50 -> TextRef.stringRes(R.string.weather_cloudy)
            else -> TextRef.stringRes(R.string.weather_overcast)
        }
    }
}
