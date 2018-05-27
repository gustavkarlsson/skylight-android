package se.gustavkarlsson.skylight.android.services_impl.formatters

import se.gustavkarlsson.skylight.android.entities.Weather
import se.gustavkarlsson.skylight.android.services.formatters.SingleValueFormatter

class WeatherFormatter : SingleValueFormatter<Weather> {
	override fun format(value: Weather): CharSequence {
		val clouds = value.cloudPercentage ?: return "?"
		val weatherPercentage = 100 - clouds
		return Integer.toString(weatherPercentage) + '%'
	}
}
