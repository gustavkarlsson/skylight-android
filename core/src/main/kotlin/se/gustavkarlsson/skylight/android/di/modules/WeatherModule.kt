package se.gustavkarlsson.skylight.android.di.modules

import io.reactivex.Flowable
import se.gustavkarlsson.skylight.android.entities.Weather
import se.gustavkarlsson.skylight.android.services.providers.WeatherProvider

interface WeatherModule {
	val weatherProvider: WeatherProvider
	val weatherFlowable: Flowable<Weather>
}
