package se.gustavkarlsson.skylight.android.lib.weather

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.Reusable
import io.reactivex.Scheduler
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import se.gustavkarlsson.skylight.android.core.Io
import se.gustavkarlsson.skylight.android.core.services.ChanceEvaluator
import se.gustavkarlsson.skylight.android.core.services.Formatter
import se.gustavkarlsson.skylight.android.core.utils.minutes
import se.gustavkarlsson.skylight.android.core.utils.seconds
import se.gustavkarlsson.skylight.android.lib.time.Time

@Module
object LibWeatherModule {

    @Provides
    @Reusable
    internal fun weatherFormatter(): Formatter<Weather> = WeatherFormatter

    @Provides
    @Reusable
    internal fun weatherEvaluator(): ChanceEvaluator<Weather> = WeatherEvaluator

    @Provides
    @Reusable
    internal fun weatherProvider(
        okHttpClient: OkHttpClient,
        @Io scheduler: Scheduler,
        time: Time
    ): WeatherProvider {
        @Suppress("EXPERIMENTAL_API_USAGE")
        val converterFactory = Json { ignoreUnknownKeys = true }
            .asConverterFactory(MediaType.get("application/json"))
        val callAdapterFactory = RxJava2CallAdapterFactory.createWithScheduler(scheduler)

        val api = Retrofit.Builder()
            .client(okHttpClient)
            .addConverterFactory(converterFactory)
            .addCallAdapterFactory(callAdapterFactory)
            .baseUrl("https://api.openweathermap.org/data/2.5/")
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
