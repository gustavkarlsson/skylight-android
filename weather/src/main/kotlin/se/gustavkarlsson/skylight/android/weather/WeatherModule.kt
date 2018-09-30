package se.gustavkarlsson.skylight.android.weather

import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
import org.koin.dsl.module.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import se.gustavkarlsson.skylight.android.entities.Location
import se.gustavkarlsson.skylight.android.entities.Report
import se.gustavkarlsson.skylight.android.entities.Weather
import se.gustavkarlsson.skylight.android.extensions.minutes
import se.gustavkarlsson.skylight.android.services.Streamable
import se.gustavkarlsson.skylight.android.services.providers.WeatherProvider

val weatherModule = module {

	single<OpenWeatherMapApi> {
		Retrofit.Builder()
			.baseUrl("https://api.openweathermap.org/data/2.5/")
			.addConverterFactory(GsonConverterFactory.create())
			.addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
			.build()
			.create(OpenWeatherMapApi::class.java)
	}

	single<WeatherProvider> {
		RetrofittedOpenWeatherMapWeatherProvider(
			get(),
			BuildConfig.OPENWEATHERMAP_API_KEY,
			5,
			get()
		)
	}

	single<Streamable<Report<Weather>>>("weather") {
		val locations = get<Flowable<Location>>("location")
		WeatherProviderStreamable(locations, get(), 15.minutes)
	}

	single<Flowable<Report<Weather>>>("weather") {
		get<Streamable<Report<Weather>>>("weather")
			.stream
			.replay(1)
			.refCount()
	}

}
