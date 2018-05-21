package se.gustavkarlsson.skylight.android.di.modules

import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import se.gustavkarlsson.skylight.android.entities.KpIndex
import se.gustavkarlsson.skylight.android.extensions.connectTimeout
import se.gustavkarlsson.skylight.android.extensions.create
import se.gustavkarlsson.skylight.android.extensions.seconds
import se.gustavkarlsson.skylight.android.services.Streamable
import se.gustavkarlsson.skylight.android.services.providers.KpIndexProvider
import se.gustavkarlsson.skylight.android.services_impl.providers.RetrofittedKpIndexProvider
import se.gustavkarlsson.skylight.android.services_impl.providers.kpindex.KpIndexApi
import se.gustavkarlsson.skylight.android.services_impl.streamables.KpIndexProviderStreamable

class RealKpIndexModule : KpIndexModule {

	private val kpIndexApi: KpIndexApi by lazy {
		Retrofit.Builder()
			.client(
				OkHttpClient.Builder()
				.connectTimeout(CONNECT_TIMEOUT)
				.build()
			)
			.baseUrl(API_URL)
			.addConverterFactory(GsonConverterFactory.create())
			.addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
			.build().create<KpIndexApi>()
	}

	override val kpIndexProvider: KpIndexProvider by lazy { RetrofittedKpIndexProvider(kpIndexApi) }

	private val kpIndexStreamable: Streamable<KpIndex> by lazy {
		KpIndexProviderStreamable(kpIndexProvider)
	}

	override val kpIndexFlowable: Flowable<KpIndex> by lazy {
		kpIndexStreamable.stream
			.replay(1)
			.refCount()
	}

	companion object {
		// TODO Make configurable in constructor
		const val API_URL = "http://api.skylight-app.net"
		val CONNECT_TIMEOUT = 20.seconds
	}
}
