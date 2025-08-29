package se.gustavkarlsson.skylight.android.lib.kpindex

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
import se.gustavkarlsson.skylight.android.lib.okhttp.OkHttpComponent
import se.gustavkarlsson.skylight.android.lib.time.Time
import se.gustavkarlsson.skylight.android.lib.time.TimeComponent
import kotlin.annotation.AnnotationTarget.CLASS
import kotlin.annotation.AnnotationTarget.FUNCTION
import kotlin.annotation.AnnotationTarget.PROPERTY_GETTER
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

@Component
@KpIndexScope
abstract class KpIndexComponent internal constructor(
    @Component internal val coreComponent: CoreComponent,
    @Component internal val timeComponent: TimeComponent,
    @Component internal val okhttpComponent: OkHttpComponent,
) {

    @get:Provides
    val kpIndexFormatter: Formatter<KpIndex> = KpIndexFormatter

    @get:Provides
    val kpIndexEvaluator: ChanceEvaluator<KpIndex> = KpIndexEvaluator

    abstract val kpIndexProvider: KpIndexProvider

    abstract val kpIndexForecastProvider: KpIndexForecastProvider

    @Provides
    @KpIndexScope
    internal fun kpIndexApi(
        okHttpClient: OkHttpClient,
    ): KpIndexApi {
        val json = Json { ignoreUnknownKeys = true }

        @Suppress("EXPERIMENTAL_API_USAGE")
        val converterFactory =
            json.asConverterFactory("application/json; charset=UTF8".toMediaType())

        return Retrofit.Builder().client(okHttpClient).addConverterFactory(converterFactory)
            .baseUrl("https://skylight-api.com/").build().create(KpIndexApi::class.java)
    }

    @Provides
    @KpIndexScope
    internal fun kpIndexProvider(
        api: KpIndexApi,
        @Io dispatcher: CoroutineDispatcher,
        time: Time,
    ): KpIndexProvider {
        val pollingInterval = 15.minutes
        val fetcher = createKpIndexFetcher(
            api = api,
            retryDelay = 15.seconds,
            pollingInterval = pollingInterval,
            dispatcher = dispatcher,
            time = time,
        )

        val expiry = (pollingInterval / 2)
        val cachePolicy = MemoryPolicy.builder<Unit, KpIndex>().setExpireAfterWrite(expiry).build()

        val store = StoreBuilder.from(fetcher).cachePolicy(cachePolicy).build()

        return StoreKpIndexProvider(store = store)
    }

    @Provides
    @KpIndexScope
    internal fun kpIndexForecastProvider(
        api: KpIndexApi,
        @Io dispatcher: CoroutineDispatcher,
    ): KpIndexForecastProvider {
        val pollingInterval = 60.minutes
        val fetcher = createKpIndexForecastFetcher(
            api = api,
            retryDelay = 15.seconds,
            pollingInterval = pollingInterval,
            dispatcher = dispatcher,
        )

        val expiry = (pollingInterval / 2)
        val cachePolicy = MemoryPolicy.builder<Unit, KpIndexForecast>().setExpireAfterWrite(expiry).build()

        val store = StoreBuilder.from(fetcher).cachePolicy(cachePolicy).build()

        return StoreKpIndexForecastProvider(store = store)
    }

    companion object {
        val instance: KpIndexComponent = KpIndexComponent::class.create(
            coreComponent = CoreComponent.instance,
            timeComponent = TimeComponent.instance,
            okhttpComponent = OkHttpComponent.instance,
        )
    }
}

@Scope
@Target(CLASS, FUNCTION, PROPERTY_GETTER)
annotation class KpIndexScope
