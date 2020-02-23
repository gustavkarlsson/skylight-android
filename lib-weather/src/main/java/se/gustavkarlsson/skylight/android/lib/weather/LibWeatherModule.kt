package se.gustavkarlsson.skylight.android.lib.weather

import dagger.Module
import dagger.Provides
import dagger.Reusable
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import se.gustavkarlsson.skylight.android.extensions.minutes
import se.gustavkarlsson.skylight.android.extensions.seconds
import se.gustavkarlsson.skylight.android.services.Time
import se.gustavkarlsson.skylight.android.services.WeatherProvider

@Module
class LibWeatherModule {

    @Provides
    @Reusable
    internal fun weatherProvider(okHttpClient: OkHttpClient, time: Time): WeatherProvider {
        val api = Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
            .build()
            .create(OpenWeatherMapApi::class.java)

        return RetrofittedOpenWeatherMapWeatherProvider(
            api = api,
            appId = BuildConfig.OPENWEATHERMAP_API_KEY,
            time = time,
            retryDelay = 15.seconds,
            pollingInterval = 15.minutes
        )
    }
}
