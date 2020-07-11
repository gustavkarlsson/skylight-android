package se.gustavkarlsson.skylight.android.lib.weather

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.Reusable
import io.reactivex.Scheduler
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import okhttp3.OkHttpClient
import retrofit2.CallAdapter
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import se.gustavkarlsson.skylight.android.Io
import se.gustavkarlsson.skylight.android.lib.time.Time
import se.gustavkarlsson.skylight.android.services.ChanceEvaluator
import se.gustavkarlsson.skylight.android.services.Formatter
import se.gustavkarlsson.skylight.android.utils.minutes
import se.gustavkarlsson.skylight.android.utils.seconds

@Module
class LibWeatherModule {

    @Provides
    @Reusable
    internal fun weatherFormatter(): Formatter<Weather> = WeatherFormatter

    @Provides
    @Reusable
    internal fun weatherEvaluator(): ChanceEvaluator<Weather> = WeatherEvaluator

    @Provides
    @Reusable
    internal fun converterFactory(): Converter.Factory {
        @Suppress("EXPERIMENTAL_API_USAGE")
        val json = Json { ignoreUnknownKeys = true }
        return json.asConverterFactory(MediaType.get("application/json"))
    }

    @Provides
    @Reusable
    internal fun callAdapterFactory(@Io scheduler: Scheduler): CallAdapter.Factory =
        RxJava2CallAdapterFactory.createWithScheduler(scheduler)

    @Provides
    @Reusable
    internal fun weatherProvider(
        okHttpClient: OkHttpClient,
        callAdapterFactory: CallAdapter.Factory,
        converterFactory: Converter.Factory,
        time: Time
    ): WeatherProvider {
        val api = Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(converterFactory)
            .addCallAdapterFactory(callAdapterFactory)
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
