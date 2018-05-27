package se.gustavkarlsson.skylight.android.services_impl.formatters

import android.content.Context
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.entities.Weather
import se.gustavkarlsson.skylight.android.services.formatters.SingleValueFormatter

class WeatherFormatter(private val context: Context) : SingleValueFormatter<Weather> {
	override fun format(value: Weather): CharSequence {
		val clouds = value.cloudPercentage ?: return "?"
		return when {
			clouds < 10 -> context.getString(R.string.weather_clear)
			clouds < 30 -> context.getString(R.string.weather_some_clouds)
			clouds < 50 -> context.getString(R.string.weather_cloudy)
			else -> context.getString(R.string.weather_overcast)
		}
	}
}
