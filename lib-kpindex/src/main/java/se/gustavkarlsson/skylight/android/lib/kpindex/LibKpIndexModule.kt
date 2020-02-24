package se.gustavkarlsson.skylight.android.lib.kpindex

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import io.reactivex.schedulers.Schedulers
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import se.gustavkarlsson.skylight.android.extensions.minutes
import se.gustavkarlsson.skylight.android.extensions.seconds
import se.gustavkarlsson.skylight.android.services.KpIndexProvider
import se.gustavkarlsson.skylight.android.services.Time
import javax.inject.Singleton

@Module
class LibKpIndexModule {

    @Suppress("EXPERIMENTAL_API_USAGE") // Json.nonstrict
    @Provides
    @Singleton
    internal fun kpIndexProvider(okHttpClient: OkHttpClient, time: Time): KpIndexProvider {
        val api = Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl("https://skylight-web-service-1.herokuapp.com/")
            .addConverterFactory(Json.nonstrict.asConverterFactory(MediaType.get("application/json")))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
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
