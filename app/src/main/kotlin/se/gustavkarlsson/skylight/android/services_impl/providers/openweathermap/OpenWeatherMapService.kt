package se.gustavkarlsson.skylight.android.services_impl.providers.openweathermap

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenWeatherMapService {
    @GET("weather")
    fun get(@Query("lat") latitude: Double, @Query("lon") longitude: Double, @Query("mode") mode: String, @Query("appid") appId: String): Call<OpenWeatherMapWeather>
}
