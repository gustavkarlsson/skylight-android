package se.gustavkarlsson.skylight.android.weather

import com.google.gson.annotations.SerializedName

internal data class OpenWeatherMapWeather(
		@SerializedName("clouds") var clouds: Clouds = Clouds()
) {
	data class Clouds(
			@SerializedName("all") var percentage: Int = -1
	)
}
