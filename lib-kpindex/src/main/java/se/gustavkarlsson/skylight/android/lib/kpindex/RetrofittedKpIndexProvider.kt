package se.gustavkarlsson.skylight.android.lib.kpindex

import com.jakewharton.rx.replayingShare
import io.reactivex.Observable
import io.reactivex.Single
import org.threeten.bp.Duration
import se.gustavkarlsson.skylight.android.entities.KpIndex
import se.gustavkarlsson.skylight.android.entities.Loadable
import se.gustavkarlsson.skylight.android.entities.Report
import se.gustavkarlsson.skylight.android.extensions.delay
import se.gustavkarlsson.skylight.android.services.KpIndexProvider
import se.gustavkarlsson.skylight.android.services.Time
import timber.log.Timber

internal class RetrofittedKpIndexProvider(
	private val api: KpIndexApi,
	private val time: Time,
	retryDelay: Duration,
	pollingInterval: Duration
) : KpIndexProvider {

	override fun get(): Single<Report<KpIndex>> =
		getReport()
			.onErrorReturnItem(Report.Error(R.string.error_no_internet_maybe, time.now()))
			.doOnSuccess { Timber.i("Provided Kp index: %s", it) }

	private val stream = getReport()
		.repeatWhen { it.delay(pollingInterval) }
		.toObservable()
		.onErrorResumeNext { e: Throwable ->
			Observable.concat<Report<KpIndex>>(
				Observable.just(
					Report.Error(R.string.error_no_internet_maybe, time.now())
				),
				Observable.error(e)
			)
		}
		.map<Loadable<Report<KpIndex>>> { Loadable.Loaded(it) }
		.retryWhen { it.delay(retryDelay) }
		.distinctUntilChanged()
		.doOnNext { Timber.i("Streamed Kp index: %s", it) }
		.replayingShare(Loadable.Loading)

	override fun stream() = stream

	private fun getReport(): Single<Report<KpIndex>> =
		api.get()
			.map<Report<KpIndex>> { Report.Success(KpIndex(it.value), time.now()) }
			.doOnError { Timber.w(it, "Failed to get Kp index from KpIndex API") }
}
