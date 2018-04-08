package se.gustavkarlsson.skylight.android.di.modules

import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
import org.threeten.bp.Duration
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import se.gustavkarlsson.skylight.android.entities.KpIndex
import se.gustavkarlsson.skylight.android.extensions.create
import se.gustavkarlsson.skylight.android.services.Streamable
import se.gustavkarlsson.skylight.android.services.providers.KpIndexProvider
import se.gustavkarlsson.skylight.android.services_impl.providers.RetrofittedKpIndexProvider
import se.gustavkarlsson.skylight.android.services_impl.providers.kpindex.KpIndexApi
import se.gustavkarlsson.skylight.android.services_impl.streamables.KpIndexProviderStreamable

class RealKpIndexModule : KpIndexModule {

	private val kpIndexApi: KpIndexApi by lazy {
		Retrofit.Builder()
			.baseUrl(API_URL)
			.addConverterFactory(GsonConverterFactory.create())
			.addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
			.build().create<KpIndexApi>()
	}

	override val kpIndexProvider: KpIndexProvider by lazy { RetrofittedKpIndexProvider(kpIndexApi) }

	override val kpIndexStreamable: Streamable<KpIndex> by lazy {
		KpIndexProviderStreamable(kpIndexProvider, POLLING_INTERVAL, RETRY_DELAY)
	}

	override val kpIndexFlowable: Flowable<KpIndex> by lazy {
		kpIndexStreamable.stream
			.replay(1)
			.refCount()
	}

	// TODO Make some configurable in constructor
	companion object {
		private const val API_URL = "https://skylight-web-service-1.herokuapp.com"
		private val POLLING_INTERVAL = Duration.ofMinutes(15)
		private val RETRY_DELAY = Duration.ofSeconds(10)
	}
}
