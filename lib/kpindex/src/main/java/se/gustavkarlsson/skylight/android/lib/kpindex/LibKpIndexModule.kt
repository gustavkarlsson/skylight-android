package se.gustavkarlsson.skylight.android.lib.kpindex

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
import se.gustavkarlsson.skylight.android.AppScope
import se.gustavkarlsson.skylight.android.Io
import se.gustavkarlsson.skylight.android.lib.time.Time
import se.gustavkarlsson.skylight.android.services.ChanceEvaluator
import se.gustavkarlsson.skylight.android.services.Formatter
import se.gustavkarlsson.skylight.android.utils.minutes
import se.gustavkarlsson.skylight.android.utils.seconds

@Module
class LibKpIndexModule {

    @Provides
    @Reusable
    internal fun kpIndexFormatter(): Formatter<KpIndex> = KpIndexFormatter

    @Provides
    @Reusable
    internal fun kpIndexEvaluator(): ChanceEvaluator<KpIndex> = KpIndexEvaluator

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
    @AppScope
    internal fun kpIndexProvider(
        okHttpClient: OkHttpClient,
        callAdapterFactory: CallAdapter.Factory,
        converterFactory: Converter.Factory,
        time: Time
    ): KpIndexProvider {
        val api = Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl("https://skylight-web-service-1.herokuapp.com/")
            .addConverterFactory(converterFactory)
            .addCallAdapterFactory(callAdapterFactory)
            .build()
            .create(KpIndexApi::class.java)

        return RetrofittedKpIndexProvider(
            api = api,
            time = time,
            retryDelay = 15.seconds,
            pollingInterval = 15.minutes
        )
    }
}
