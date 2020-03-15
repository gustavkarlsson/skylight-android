package se.gustavkarlsson.skylight.android.lib.weather

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.Reusable
import io.reactivex.schedulers.Schedulers
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import se.gustavkarlsson.skylight.android.entities.Weather
import se.gustavkarlsson.skylight.android.extensions.minutes
import se.gustavkarlsson.skylight.android.extensions.seconds
import se.gustavkarlsson.skylight.android.services.ChanceEvaluator
import se.gustavkarlsson.skylight.android.services.Formatter
import se.gustavkarlsson.skylight.android.services.Time
import se.gustavkarlsson.skylight.android.services.WeatherProvider

@Module
class LibWeatherModule {

    @Provides
    @Reusable
    internal fun weatherFormatter(): Formatter<Weather> = WeatherFormatter

    @Provides
    @Reusable
    internal fun weatherEvaluator(): ChanceEvaluator<Weather> = WeatherEvaluator

    @Suppress("EXPERIMENTAL_API_USAGE") // Json.nonstrict
    @Provides
    @Reusable
    internal fun weatherProvider(okHttpClient: OkHttpClient, time: Time): WeatherProvider {
        val api = Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(Json.nonstrict.asConverterFactory(MediaType.get("application/json")))
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
