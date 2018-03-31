package se.gustavkarlsson.skylight.android.dagger.modules

import dagger.Module
import dagger.Provides
import dagger.Reusable
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

@Module
class KpIndexModule {

	@Provides
	@Reusable
	fun provideKpIndexService(): KpIndexApi {
		return Retrofit.Builder()
			.baseUrl(API_URL)
			.addConverterFactory(GsonConverterFactory.create())
			.addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
			.build().create()
	}

	@Provides
	@Reusable
	fun provideKpIndexProvider(
		kpIndexApi: KpIndexApi
	): KpIndexProvider = RetrofittedKpIndexProvider(kpIndexApi)

	@Provides
	@Reusable
	fun provideKpIndexStreamable(
		provider: KpIndexProvider
	): Streamable<KpIndex> = KpIndexProviderStreamable(provider, POLLING_INTERVAL, RETRY_DELAY)

	@Provides
	@Reusable
	fun provideKpIndexFlowable(
		streamable: Streamable<KpIndex>
	): Flowable<KpIndex> = streamable.stream

	companion object {
		private const val API_URL = "https://skylight-web-service-1.herokuapp.com"
		private val POLLING_INTERVAL = Duration.ofMinutes(15)
		private val RETRY_DELAY = Duration.ofSeconds(10)
	}
}
