package se.gustavkarlsson.skylight.android.kpindex

import io.reactivex.Flowable
import io.reactivex.Single
import org.threeten.bp.Duration
import se.gustavkarlsson.skylight.android.entities.KpIndex
import se.gustavkarlsson.skylight.android.entities.Report
import se.gustavkarlsson.skylight.android.extensions.delay
import se.gustavkarlsson.skylight.android.services.providers.KpIndexProvider
import se.gustavkarlsson.skylight.android.services.providers.Time
import timber.log.Timber

internal class RetrofittedKpIndexProvider(
	private val api: KpIndexApi,
	private val time: Time,
	retryDelay: Duration,
	pollingInterval: Duration
) : KpIndexProvider {

	private fun getReport(): Single<Report<KpIndex>> =
		api.get()
			.map { Report.success(KpIndex(it.value), time.now().blockingGet()) }
			.doOnError { Timber.w(it, "Failed to get Kp index from KpIndex API") }

	override fun get(): Single<Report<KpIndex>> =
		getReport()
			.onErrorReturnItem(Report.error(R.string.error_no_internet_maybe, time.now().blockingGet()))
			.doOnSuccess { Timber.i("Provided Kp index: %s", it) }

	override val stream: Flowable<Report<KpIndex>> =
		getReport()
			.repeatWhen { it.delay(pollingInterval) }
			.onErrorResumeNext { it: Throwable ->
				Flowable.error<Report<KpIndex>>(it)
					.startWith(Report.error(R.string.error_no_internet_maybe, time.now().blockingGet()))
			}
			.retryWhen { it.delay(retryDelay) }
			.distinctUntilChanged()
			.doOnNext { Timber.i("Streamed Kp index: %s", it) }
			.replay(1)
			.refCount()
}
