package se.gustavkarlsson.skylight.android.lib.kpindex

import com.dropbox.android.external.store4.MemoryPolicy
import com.dropbox.android.external.store4.StoreBuilder
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides
import dagger.Reusable
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import se.gustavkarlsson.skylight.android.core.AppScopeMarker
import se.gustavkarlsson.skylight.android.core.Io
import se.gustavkarlsson.skylight.android.core.services.ChanceEvaluator
import se.gustavkarlsson.skylight.android.core.services.Formatter
import se.gustavkarlsson.skylight.android.core.utils.minutes
import se.gustavkarlsson.skylight.android.core.utils.seconds
import se.gustavkarlsson.skylight.android.lib.time.Time
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.ExperimentalTime

@Module
@ContributesTo(AppScopeMarker::class)
object LibKpIndexModule {

    @Provides
    internal fun kpIndexFormatter(): Formatter<KpIndex> = KpIndexFormatter

    @Provides
    internal fun kpIndexEvaluator(): ChanceEvaluator<KpIndex> = KpIndexEvaluator

    @OptIn(
        ExperimentalTime::class,
        ExperimentalSerializationApi::class,
    )
    @Provides
    @Reusable
    internal fun kpIndexProvider(
        okHttpClient: OkHttpClient,
        @Io dispatcher: CoroutineDispatcher,
        time: Time,
    ): KpIndexProvider {
        val json = Json { ignoreUnknownKeys = true }

        @Suppress("EXPERIMENTAL_API_USAGE")
        val converterFactory = json.asConverterFactory(MediaType.get("application/json"))

        val api = Retrofit.Builder()
            .client(okHttpClient)
            .addConverterFactory(converterFactory)
            .baseUrl("https://skylight-web-service-1.herokuapp.com/")
            .build()
            .create(KpIndexApi::class.java)

        val pollingInterval = 15.minutes
        val fetcher = createKpIndexFetcher(
            api = api,
            retryDelay = 15.seconds,
            pollingInterval = pollingInterval,
            dispatcher = dispatcher,
            time = time,
        )

        val expiry = (pollingInterval.toMillis() / 2).milliseconds
        val cachePolicy = MemoryPolicy.builder<Unit, KpIndex>()
            .setExpireAfterWrite(expiry)
            .build()

        val store = StoreBuilder.from(fetcher)
            .cachePolicy(cachePolicy)
            .build()

        return StoreKpIndexProvider(store = store)
    }
}
