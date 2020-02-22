package se.gustavkarlsson.skylight.android.lib.kpindex

import dagger.Module
import dagger.Provides
import dagger.Reusable
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import org.koin.dsl.module.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import se.gustavkarlsson.skylight.android.extensions.minutes
import se.gustavkarlsson.skylight.android.extensions.seconds
import se.gustavkarlsson.skylight.android.services.KpIndexProvider
import se.gustavkarlsson.skylight.android.services.Time
import javax.inject.Singleton

val libKpIndexModule = module {

    single<KpIndexApi> {
        Retrofit.Builder()
            .client(get())
            .baseUrl("https://skylight-web-service-1.herokuapp.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
            .build()
            .create(KpIndexApi::class.java)
    }

    single<KpIndexProvider> {
        RetrofittedKpIndexProvider(
            api = get(),
            time = get(),
            retryDelay = 15.seconds,
            pollingInterval = 15.minutes
        )
    }
}

@Module
class LibKpIndexModule {

    @Provides
    @Singleton
    internal fun kpIndexProvider(okHttpClient: OkHttpClient, time: Time): KpIndexProvider {
        val api = Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl("https://skylight-web-service-1.herokuapp.com/")
            .addConverterFactory(GsonConverterFactory.create())
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
