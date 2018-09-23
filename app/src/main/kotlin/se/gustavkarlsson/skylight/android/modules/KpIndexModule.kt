package se.gustavkarlsson.skylight.android.modules

import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import org.koin.dsl.module.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import se.gustavkarlsson.skylight.android.entities.KpIndex
import se.gustavkarlsson.skylight.android.entities.Report
import se.gustavkarlsson.skylight.android.extensions.*
import se.gustavkarlsson.skylight.android.services.Streamable
import se.gustavkarlsson.skylight.android.services.providers.KpIndexProvider
import se.gustavkarlsson.skylight.android.services_impl.providers.RetrofittedKpIndexProvider
import se.gustavkarlsson.skylight.android.services_impl.providers.kpindex.KpIndexApi
import se.gustavkarlsson.skylight.android.services_impl.streamables.KpIndexProviderStreamable

val kpIndexModule = module {

	single<KpIndexApi> {
		val timeout = 30.seconds
		Retrofit.Builder()
			.client(
				OkHttpClient.Builder()
					.connectTimeout(timeout)
					.readTimeout(timeout)
					.writeTimeout(timeout)
					.build()
			)
			.baseUrl("https://skylight-web-service-1.herokuapp.com/")
			.addConverterFactory(GsonConverterFactory.create())
			.addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
			.build().create()
	}

	single<KpIndexProvider> {
		RetrofittedKpIndexProvider(get(), 5, get())
	}

	single<Streamable<Report<KpIndex>>>("kpIndex") {
		KpIndexProviderStreamable(get(), 15.minutes)
	}

	single<Flowable<Report<KpIndex>>>("kpIndex") {
		get<Streamable<Report<KpIndex>>>("kpIndex")
			.stream
			.replay(1)
			.refCount()
	}

}
