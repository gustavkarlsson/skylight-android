package se.gustavkarlsson.skylight.android.lib.weather

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

internal interface OpenWeatherMapApi {
	@GET("weather")
	fun get(
		@Query("lat") latitude: Double,
		@Query("lon") longitude: Double,
		@Query("mode") mode: String,
		@Query("appid") appId: String
	): Single<OpenWeatherMapWeather>
}
