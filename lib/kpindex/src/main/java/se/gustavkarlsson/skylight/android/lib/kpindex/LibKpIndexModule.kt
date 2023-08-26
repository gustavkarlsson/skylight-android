package se.gustavkarlsson.skylight.android.lib.kpindex

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
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
import se.gustavkarlsson.skylight.android.core.AppScopeMarker
import se.gustavkarlsson.skylight.android.core.Io
import se.gustavkarlsson.skylight.android.core.services.ChanceEvaluator
import se.gustavkarlsson.skylight.android.core.services.Formatter
import se.gustavkarlsson.skylight.android.lib.time.Time
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

@Module
@ContributesTo(AppScopeMarker::class)
object LibKpIndexModule {

    @Provides
    internal fun kpIndexFormatter(): Formatter<KpIndex> = KpIndexFormatter

    @Provides
    internal fun kpIndexEvaluator(): ChanceEvaluator<KpIndex> = KpIndexEvaluator

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
            .baseUrl("https://skylight-api.com/")
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

        val expiry = (pollingInterval / 2)
        val cachePolicy = MemoryPolicy.builder<Unit, KpIndex>()
            .setExpireAfterWrite(expiry)
            .build()

        val store = StoreBuilder.from(fetcher)
            .cachePolicy(cachePolicy)
            .build()

        return StoreKpIndexProvider(store = store)
    }
}
