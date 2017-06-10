package se.gustavkarlsson.skylight.android.background.providers.impl.openweathermap

import com.google.gson.annotations.SerializedName

data class OpenWeatherMapWeather(
		@SerializedName("clouds") var clouds: Clouds = OpenWeatherMapWeather.Clouds()
) {
	data class Clouds(
			@SerializedName("all") var percentage: Int = -1
	)
}
