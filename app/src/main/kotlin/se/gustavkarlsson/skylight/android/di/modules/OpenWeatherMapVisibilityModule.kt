package se.gustavkarlsson.skylight.android.di.modules

import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import se.gustavkarlsson.skylight.android.entities.Visibility
import se.gustavkarlsson.skylight.android.extensions.create
import se.gustavkarlsson.skylight.android.services.Streamable
import se.gustavkarlsson.skylight.android.services.providers.VisibilityProvider
import se.gustavkarlsson.skylight.android.services_impl.providers.RetrofittedOpenWeatherMapVisibilityProvider
import se.gustavkarlsson.skylight.android.services_impl.providers.openweathermap.OpenWeatherMapApi
import se.gustavkarlsson.skylight.android.services_impl.streamables.VisibilityProviderStreamable

class OpenWeatherMapVisibilityModule(
	apiKey: String,
	locationModule: LocationModule
) : VisibilityModule {

	override val visibilityProvider: VisibilityProvider by lazy {
		val api = Retrofit.Builder()
			.baseUrl(API_URL)
			.addConverterFactory(GsonConverterFactory.create())
			.addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
			.build().create<OpenWeatherMapApi>()
		RetrofittedOpenWeatherMapVisibilityProvider(api, apiKey
		)
	}

	private val visibilityStreamable: Streamable<Visibility> by lazy {
		VisibilityProviderStreamable(
			locationModule.locationFlowable,
			visibilityProvider
		)
	}

	override val visibilityFlowable: Flowable<Visibility> by lazy {
		visibilityStreamable.stream
			.replay(1)
			.refCount()
	}

	companion object {
		// TODO Make configurable in constructor
		const val API_URL = "http://api.openweathermap.org/data/2.5/"
	}
}
