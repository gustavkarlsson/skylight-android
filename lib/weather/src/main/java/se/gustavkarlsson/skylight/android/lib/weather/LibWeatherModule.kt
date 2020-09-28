package se.gustavkarlsson.skylight.android.lib.weather

import com.dropbox.android.external.store4.MemoryPolicy
import com.dropbox.android.external.store4.StoreBuilder
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.Reusable
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import se.gustavkarlsson.skylight.android.core.Io
import se.gustavkarlsson.skylight.android.core.services.ChanceEvaluator
import se.gustavkarlsson.skylight.android.core.services.Formatter
import se.gustavkarlsson.skylight.android.core.utils.minutes
import se.gustavkarlsson.skylight.android.core.utils.seconds
import se.gustavkarlsson.skylight.android.lib.location.Location
import se.gustavkarlsson.skylight.android.lib.time.Time
import kotlin.time.ExperimentalTime
import kotlin.time.milliseconds as kotlinMilliseconds

@Module
object LibWeatherModule {

    @Provides
    @Reusable
    internal fun weatherFormatter(): Formatter<Weather> = WeatherFormatter

    @Provides
    @Reusable
    internal fun weatherEvaluator(): ChanceEvaluator<Weather> = WeatherEvaluator

    @ExperimentalTime
    @FlowPreview
    @ExperimentalCoroutinesApi
    @Provides
    @Reusable
    internal fun weatherProvider(
        okHttpClient: OkHttpClient,
        @Io dispatcher: CoroutineDispatcher,
        time: Time
    ): WeatherProvider {
        @Suppress("EXPERIMENTAL_API_USAGE")
        val converterFactory = Json { ignoreUnknownKeys = true }
            .asConverterFactory(MediaType.get("application/json"))

        val api = Retrofit.Builder()
            .client(okHttpClient)
            .addConverterFactory(converterFactory)
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build()
            .create(OpenWeatherMapApi::class.java)

        val pollingInterval = 15.minutes
        val createFetcher = createOpenWeatherMapFetcher(
            api = api,
            appId = BuildConfig.OPENWEATHERMAP_API_KEY,
            retryDelay = 15.seconds,
            pollingInterval = pollingInterval,
            dispatcher = dispatcher
        )

        val expiry = (pollingInterval.toMillis() / 2).kotlinMilliseconds
        val cachePolicy = MemoryPolicy.builder<Location, Weather>()
            .setExpireAfterWrite(expiry)
            .setMaxSize(16)
            .build()

        val store = StoreBuilder.from(createFetcher)
            .cachePolicy(cachePolicy)
            .build()

        return StoreWeatherProvider(
            store = store,
            time = time
        )
    }
}
