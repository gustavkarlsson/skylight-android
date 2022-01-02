package se.gustavkarlsson.skylight.android.lib.weather

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

internal interface OpenWeatherMapApi {
    @GET("weather")
    suspend fun get(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("mode") mode: String,
        @Query("appid") appId: String,
    ): Response<OpenWeatherMapWeather>
}
