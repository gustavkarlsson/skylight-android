package se.gustavkarlsson.skylight.android.di.modules

import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import se.gustavkarlsson.skylight.android.BuildConfig
import se.gustavkarlsson.skylight.android.entities.Weather
import se.gustavkarlsson.skylight.android.extensions.create
import se.gustavkarlsson.skylight.android.services.Streamable
import se.gustavkarlsson.skylight.android.services.providers.WeatherProvider
import se.gustavkarlsson.skylight.android.services_impl.providers.RetrofittedOpenWeatherMapWeatherProvider
import se.gustavkarlsson.skylight.android.services_impl.providers.openweathermap.OpenWeatherMapApi
import se.gustavkarlsson.skylight.android.services_impl.streamables.WeatherProviderStreamable

class OpenWeatherMapWeatherModule(
	locationModule: LocationModule,
	apiUrl: String = "http://api.openweathermap.org/data/2.5/",
	apiKey: String = BuildConfig.OPENWEATHERMAP_API_KEY
) : WeatherModule {

	override val weatherProvider: WeatherProvider by lazy {
		val api = Retrofit.Builder()
			.baseUrl(apiUrl)
			.addConverterFactory(GsonConverterFactory.create())
			.addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
			.build().create<OpenWeatherMapApi>()
		RetrofittedOpenWeatherMapWeatherProvider(api, apiKey
		)
	}

	private val weatherStreamable: Streamable<Weather> by lazy {
		WeatherProviderStreamable(
			locationModule.locationFlowable,
			weatherProvider
		)
	}

	override val weatherFlowable: Flowable<Weather> by lazy {
		weatherStreamable.stream
			.replay(1)
			.refCount()
	}
}
