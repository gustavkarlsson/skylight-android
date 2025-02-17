package se.gustavkarlsson.skylight.android.lib.weather

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.serialization.json.Json
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides
import me.tatarka.inject.annotations.Scope
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import org.mobilenativefoundation.store.store5.MemoryPolicy
import org.mobilenativefoundation.store.store5.StoreBuilder
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import se.gustavkarlsson.skylight.android.core.CoreComponent
import se.gustavkarlsson.skylight.android.core.Io
import se.gustavkarlsson.skylight.android.core.services.ChanceEvaluator
import se.gustavkarlsson.skylight.android.core.services.Formatter
import se.gustavkarlsson.skylight.android.lib.location.ApproximatedLocation
import se.gustavkarlsson.skylight.android.lib.okhttp.OkHttpComponent
import se.gustavkarlsson.skylight.android.lib.time.Time
import se.gustavkarlsson.skylight.android.lib.time.TimeComponent
import kotlin.annotation.AnnotationTarget.CLASS
import kotlin.annotation.AnnotationTarget.FUNCTION
import kotlin.annotation.AnnotationTarget.PROPERTY_GETTER
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

@Component
@WeatherScope
abstract class WeatherComponent internal constructor(
    @Component internal val coreComponent: CoreComponent,
    @Component internal val okhttpComponent: OkHttpComponent,
    @Component internal val timeComponent: TimeComponent,
) {

    abstract val weatherProvider: WeatherProvider

    @get:Provides
    val weatherFormatter: Formatter<Weather> = WeatherFormatter

    @get:Provides
    val weatherEvaluator: ChanceEvaluator<Weather> = WeatherEvaluator

    @Provides
    @WeatherScope
    internal fun weatherProvider(
        okHttpClient: OkHttpClient,
        @Io dispatcher: CoroutineDispatcher,
        time: Time,
    ): WeatherProvider {
        val json = Json { ignoreUnknownKeys = true }

        @Suppress("EXPERIMENTAL_API_USAGE")
        val converterFactory = json.asConverterFactory("application/json; charset=UTF8".toMediaType())

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

    companion object {
        val instance: WeatherComponent = WeatherComponent::class.create(
            coreComponent = CoreComponent.instance,
            okhttpComponent = OkHttpComponent.instance,
            timeComponent = TimeComponent.instance,
        )
    }
}

@Scope
@Target(CLASS, FUNCTION, PROPERTY_GETTER)
annotation class WeatherScope
