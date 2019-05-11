package se.gustavkarlsson.skylight.android.kpindex

import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.threeten.bp.Duration
import se.gustavkarlsson.skylight.android.entities.KpIndex
import se.gustavkarlsson.skylight.android.entities.Report
import se.gustavkarlsson.skylight.android.extensions.delay
import se.gustavkarlsson.skylight.android.services.providers.KpIndexProvider
import se.gustavkarlsson.skylight.android.services.providers.Time
import timber.log.Timber

internal class RetrofittedKpIndexProvider(
	private val api: KpIndexApi,
	private val retryCount: Long,
	private val pollingInterval: Duration,
	private val time: Time
) : KpIndexProvider {

	override fun get(): Single<Report<KpIndex>> =
		api.get()
			.subscribeOn(Schedulers.io())
			.map { Report.success(KpIndex(it.value), time.now().blockingGet()) }
			.doOnError { Timber.w(it, "Failed to get Kp index from KpIndex API") }
			.retry(retryCount)
			.doOnError { Timber.e(it, "Failed to get Kp index from KpIndex API after retrying %d times", retryCount) }
			.onErrorReturnItem(Report.error(R.string.error_no_internet_maybe, time.now().blockingGet()))
			.doOnSuccess { Timber.i("Provided Kp index: %s", it) }

	override val stream: Flowable<Report<KpIndex>> =
		get()
			.repeatWhen { it.delay(pollingInterval) }
			.distinctUntilChanged()
			.doOnNext { Timber.i("Streamed Kp index: %s", it) }
			.replay(1)
			.refCount()
}
