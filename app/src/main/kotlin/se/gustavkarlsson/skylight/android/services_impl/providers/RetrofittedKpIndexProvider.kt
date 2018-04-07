package se.gustavkarlsson.skylight.android.services_impl.providers

import dagger.Reusable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import se.gustavkarlsson.skylight.android.entities.KpIndex
import se.gustavkarlsson.skylight.android.services.providers.KpIndexProvider
import se.gustavkarlsson.skylight.android.services_impl.providers.kpindex.KpIndexApi
import timber.log.Timber
import javax.inject.Inject

@Reusable
class RetrofittedKpIndexProvider
@Inject
constructor(
	private val api: KpIndexApi
) : KpIndexProvider {

	override fun get(): Single<KpIndex> {
		return api.get()
			.subscribeOn(Schedulers.io())
			.map { KpIndex(it.value.toDouble()) }
			.doOnError { Timber.w(it, "Failed to get Kp index from KpIndex API") }
			.onErrorReturnItem(KpIndex())
			.doOnSuccess { Timber.i("Provided Kp index: %s", it) }
	}
}
