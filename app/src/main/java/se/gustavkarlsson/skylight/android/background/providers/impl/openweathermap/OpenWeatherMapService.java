package se.gustavkarlsson.skylight.android.background.providers.impl.openweathermap;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface OpenWeatherMapService {
	@GET("weather")
	Call<OpenWeatherMapWeather> get(@Query("lat") double latitude, @Query("lon") double longitude, @Query("mode") String mode, @Query("appid") String appId);
}
