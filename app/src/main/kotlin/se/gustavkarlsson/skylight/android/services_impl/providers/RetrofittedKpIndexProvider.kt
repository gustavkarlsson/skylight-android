package se.gustavkarlsson.skylight.android.services_impl.providers

import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import se.gustavkarlsson.skylight.android.entities.KpIndex
import se.gustavkarlsson.skylight.android.services.providers.KpIndexProvider
import se.gustavkarlsson.skylight.android.services_impl.providers.kpindex.KpIndexApi
import timber.log.Timber

class RetrofittedKpIndexProvider(
	private val api: KpIndexApi,
	private val retryCount: Long = 5
) : KpIndexProvider {

	override fun get(): Single<KpIndex> {
		return api.get()
			.subscribeOn(Schedulers.io())
			.map { KpIndex(it.value.toDouble()) }
			.doOnError { Timber.w(it, "Failed to get Kp index from KpIndex API") }
			.retry(retryCount)
			.doOnError { Timber.e(it, "Failed to get Kp index from KpIndex API after retrying $retryCount times") }
			.onErrorReturnItem(KpIndex())
			.doOnSuccess { Timber.i("Provided Kp index: %s", it) }
	}
}
