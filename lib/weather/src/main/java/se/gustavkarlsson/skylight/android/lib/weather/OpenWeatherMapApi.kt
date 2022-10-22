package se.gustavkarlsson.skylight.android.lib.weather

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

internal interface OpenWeatherMapApi {
    @GET("weather")
    suspend fun getWeather(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") appId: String,
    ): Response<OpenWeatherMapWeather>

    @GET("forecast")
    suspend fun getWeatherForecast(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") appId: String,
    ): Response<OpenWeatherMapWeatherForecast>
}
