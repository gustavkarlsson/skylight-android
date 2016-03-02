package se.gustavkarlsson.aurora_notifier.android.services;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Query;

public interface OpenWeatherMapService {

	String BASE_URL = "http://api.openweathermap.org/data/2.5/";
	String APP_ID = "317cc1cbab742dfda3c96c93e7873b6e";

	@GET("weather")
	Call<Weather> get(@Query("lat") double latitude, @Query("lon") double longitude, @Query("mode") String mode, @Query("appid") String appId);
}
