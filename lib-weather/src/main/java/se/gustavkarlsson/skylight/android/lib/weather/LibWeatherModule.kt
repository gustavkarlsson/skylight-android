package se.gustavkarlsson.skylight.android.lib.weather

import io.reactivex.schedulers.Schedulers
import org.koin.dsl.module.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import se.gustavkarlsson.skylight.android.extensions.minutes
import se.gustavkarlsson.skylight.android.extensions.seconds
import se.gustavkarlsson.skylight.android.services.WeatherProvider

val libWeatherModule = module {

	single<OpenWeatherMapApi> {
		Retrofit.Builder()
			.client(get())
			.baseUrl("https://api.openweathermap.org/data/2.5/")
			.addConverterFactory(GsonConverterFactory.create())
			.addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
			.build()
			.create(OpenWeatherMapApi::class.java)
	}

	single<WeatherProvider> {
		RetrofittedOpenWeatherMapWeatherProvider(
			api = get(),
			appId = BuildConfig.OPENWEATHERMAP_API_KEY,
			time = get(),
			retryDelay = 15.seconds,
			pollingInterval = 15.minutes
		)
	}

}
