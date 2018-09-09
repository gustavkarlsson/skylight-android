package se.gustavkarlsson.skylight.android

import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
import org.koin.dsl.module.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import se.gustavkarlsson.skylight.android.entities.Location
import se.gustavkarlsson.skylight.android.entities.Weather
import se.gustavkarlsson.skylight.android.extensions.create
import se.gustavkarlsson.skylight.android.extensions.minutes
import se.gustavkarlsson.skylight.android.extensions.seconds
import se.gustavkarlsson.skylight.android.services.Streamable
import se.gustavkarlsson.skylight.android.services.providers.WeatherProvider
import se.gustavkarlsson.skylight.android.services_impl.providers.RetrofittedOpenWeatherMapWeatherProvider
import se.gustavkarlsson.skylight.android.services_impl.providers.openweathermap.OpenWeatherMapApi
import se.gustavkarlsson.skylight.android.services_impl.streamables.WeatherProviderStreamable

val weatherModule = module {

	single<OpenWeatherMapApi> {
		Retrofit.Builder()
			.baseUrl("http://api.openweathermap.org/data/2.5/")
			.addConverterFactory(GsonConverterFactory.create())
			.addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
			.build().create()
	}

	single<WeatherProvider> {
		RetrofittedOpenWeatherMapWeatherProvider(get(), BuildConfig.OPENWEATHERMAP_API_KEY, 5)
	}

	single<Streamable<Weather>>("weather") {
		val locations = get<Flowable<Location>>("location")
		WeatherProviderStreamable(locations, get(), 15.minutes, 10.seconds)
	}

	single<Flowable<Weather>>("weather") {
		get<Streamable<Weather>>("weather")
			.stream
			.replay(1)
			.refCount()
	}

}
