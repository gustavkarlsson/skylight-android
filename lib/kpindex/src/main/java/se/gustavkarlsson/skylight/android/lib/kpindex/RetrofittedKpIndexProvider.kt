package se.gustavkarlsson.skylight.android.lib.kpindex

import com.jakewharton.rx.replayingShare
import io.reactivex.Observable
import io.reactivex.Single
import org.threeten.bp.Duration
import se.gustavkarlsson.skylight.android.entities.Cause
import se.gustavkarlsson.skylight.android.entities.Loadable
import se.gustavkarlsson.skylight.android.entities.Report
import se.gustavkarlsson.skylight.android.lib.time.Time
import se.gustavkarlsson.skylight.android.utils.delay
import timber.log.Timber
import java.io.IOException

internal class RetrofittedKpIndexProvider(
    private val api: KpIndexApi,
    private val time: Time,
    retryDelay: Duration,
    pollingInterval: Duration
) : KpIndexProvider {

    override fun get(): Single<Report<KpIndex>> =
        getReport()
            .onErrorReturn { throwable ->
                Report.Error(getCause(throwable), time.now())
            }
            .doOnSuccess { Timber.i("Provided Kp index: %s", it) }

    private val stream = getReport()
        .repeatWhen { it.delay(pollingInterval) }
        .toObservable()
        .onErrorResumeNext { throwable: Throwable ->
            val cause = getCause(throwable)
            Observable.concat<Report<KpIndex>>(
                Observable.just(Report.Error(cause, time.now())),
                Observable.error(throwable)
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

private fun getCause(throwable: Throwable): Cause =
    when (throwable) {
        is IOException -> Cause.Connectivity
        else -> Cause.Unknown
    }
