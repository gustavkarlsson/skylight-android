package se.gustavkarlsson.skylight.android.kpindex

import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import se.gustavkarlsson.skylight.android.entities.KpIndex
import se.gustavkarlsson.skylight.android.entities.Report
import se.gustavkarlsson.skylight.android.services.providers.KpIndexProvider
import se.gustavkarlsson.skylight.android.services.providers.TimeProvider
import timber.log.Timber

class RetrofittedKpIndexProvider(
	private val api: KpIndexApi,
	private val retryCount: Long,
	private val timeProvider: TimeProvider
) : KpIndexProvider {

	override fun get(): Single<Report<KpIndex>> {
		return api.get()
			.subscribeOn(Schedulers.io())
			.map { Report.success(KpIndex(it.value), timeProvider.getTime().blockingGet()) }
			.doOnError { Timber.w(it, "Failed to get Kp index from KpIndex API") }
			.retry(retryCount)
			.doOnError { Timber.e(it, "Failed to get Kp index from KpIndex API after retrying $retryCount times") }
			.onErrorReturnItem(Report.error(R.string.error_no_internet_maybe, timeProvider.getTime().blockingGet()))
			.doOnSuccess { Timber.i("Provided Kp index: %s", it) }
	}
}
