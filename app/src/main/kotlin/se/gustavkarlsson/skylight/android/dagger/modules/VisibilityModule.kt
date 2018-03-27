package se.gustavkarlsson.skylight.android.dagger.modules

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.Reusable
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.entities.Location
import se.gustavkarlsson.skylight.android.entities.Visibility
import se.gustavkarlsson.skylight.android.extensions.create
import se.gustavkarlsson.skylight.android.services.Streamable
import se.gustavkarlsson.skylight.android.services.providers.VisibilityProvider
import se.gustavkarlsson.skylight.android.services_impl.providers.openweathermap.OpenWeatherMapApi
import se.gustavkarlsson.skylight.android.services_impl.providers.openweathermap.RetrofittedOpenWeatherMapVisibilityProvider
import se.gustavkarlsson.skylight.android.services_impl.streamables.VisibilityStreamable

@Module
class VisibilityModule {

	@Provides
	@Reusable
	fun provideOpenWeatherMapService(): OpenWeatherMapApi {
		return Retrofit.Builder()
			.baseUrl(OPENWEATHERMAP_API_URL)
			.addConverterFactory(GsonConverterFactory.create())
			.addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
			.build().create()
	}

	@Provides
	@Reusable
	fun provideVisibilityProvider(
		context: Context,
		openWeatherMapApi: OpenWeatherMapApi
	): VisibilityProvider {
		val apiKey = context.getString(R.string.api_key_openweathermap)
		return RetrofittedOpenWeatherMapVisibilityProvider(openWeatherMapApi, apiKey)
	}

	@Provides
	@Reusable
	fun provideVisibilityStreamable(
		locations: Flowable<Location>,
		provider: VisibilityProvider
	): Streamable<Visibility> = VisibilityStreamable(locations, provider)

	@Provides
	@Reusable
	fun provideVisibilityFlowable(
		streamable: Streamable<Visibility>
	): Flowable<Visibility> = streamable.stream

	companion object {
		private const val OPENWEATHERMAP_API_URL = "http://api.openweathermap.org/data/2.5/"
	}
}
