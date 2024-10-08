package se.gustavkarlsson.skylight.android.lib.weather

import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides
import dagger.Reusable
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import okhttp3.OkHttpClient
import org.mobilenativefoundation.store.store5.MemoryPolicy
import org.mobilenativefoundation.store.store5.StoreBuilder
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import se.gustavkarlsson.skylight.android.core.AppScopeMarker
import se.gustavkarlsson.skylight.android.core.Io
import se.gustavkarlsson.skylight.android.core.services.ChanceEvaluator
import se.gustavkarlsson.skylight.android.core.services.Formatter
import se.gustavkarlsson.skylight.android.lib.location.ApproximatedLocation
import se.gustavkarlsson.skylight.android.lib.time.Time
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

@Module
@ContributesTo(AppScopeMarker::class)
object LibWeatherModule {

    @Provides
    internal fun weatherFormatter(): Formatter<Weather> = WeatherFormatter

    @Provides
    internal fun weatherEvaluator(): ChanceEvaluator<Weather> = WeatherEvaluator

    @Provides
    @Reusable
    internal fun weatherProvider(
        okHttpClient: OkHttpClient,
        @Io dispatcher: CoroutineDispatcher,
        time: Time,
    ): WeatherProvider {
        val json = Json { ignoreUnknownKeys = true }

        @Suppress("EXPERIMENTAL_API_USAGE")
        val converterFactory = json.asConverterFactory(MediaType.get("application/json; charset=UTF8"))

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
            dispatcher = dispatcher,
            time = time,
        )

        val expiry = (pollingInterval / 2)
        val cachePolicy = MemoryPolicy.builder<ApproximatedLocation, Weather>()
            .setExpireAfterWrite(expiry)
            .setMaxSize(16)
            .build()

        val store = StoreBuilder.from(createFetcher)
            .cachePolicy(cachePolicy)
            .build()

        return StoreWeatherProvider(
            store = store,
            approximationMeters = 1000.0,
        )
    }
}
