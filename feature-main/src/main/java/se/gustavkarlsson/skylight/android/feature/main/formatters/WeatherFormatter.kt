package se.gustavkarlsson.skylight.android.feature.main.formatters

import com.ioki.textref.TextRef
import se.gustavkarlsson.skylight.android.entities.Weather
import se.gustavkarlsson.skylight.android.feature.main.R
import se.gustavkarlsson.skylight.android.services.Formatter

internal object WeatherFormatter : Formatter<Weather> {
	override fun format(value: Weather): TextRef {
		val clouds = value.cloudPercentage
		return when {
			clouds < 10 -> TextRef(R.string.main_weather_clear)
			clouds < 30 -> TextRef(R.string.main_weather_some_clouds)
			clouds < 50 -> TextRef(R.string.main_weather_cloudy)
			else -> TextRef(R.string.main_weather_overcast)
		}
	}
}
