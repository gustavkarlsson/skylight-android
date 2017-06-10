package se.gustavkarlsson.skylight.android.background.providers.impl.openweathermap

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenWeatherMapService {
    @GET("weather")
    fun get(@Query("lat") latitude: Double, @Query("lon") longitude: Double, @Query("mode") mode: String, @Query("appid") appId: String): Call<OpenWeatherMapWeather>
}
