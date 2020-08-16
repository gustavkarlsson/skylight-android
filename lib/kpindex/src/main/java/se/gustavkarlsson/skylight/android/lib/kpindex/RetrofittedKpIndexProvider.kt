package se.gustavkarlsson.skylight.android.lib.kpindex

import com.jakewharton.rx.replayingShare
import io.reactivex.Observable
import io.reactivex.Single
import org.threeten.bp.Duration
import se.gustavkarlsson.skylight.android.core.entities.Cause
import se.gustavkarlsson.skylight.android.core.entities.Loadable
import se.gustavkarlsson.skylight.android.core.entities.Report
import se.gustavkarlsson.skylight.android.core.logging.logError
import se.gustavkarlsson.skylight.android.core.logging.logInfo
import se.gustavkarlsson.skylight.android.core.logging.logWarn
import se.gustavkarlsson.skylight.android.core.utils.delay
import se.gustavkarlsson.skylight.android.lib.time.Time
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
            .doOnSuccess { logInfo { "Provided Kp index: $it" } }

    private val stream = getReport()
        .repeatWhen { it.delay(pollingInterval) }
        .toObservable()
        .onErrorResumeNext { throwable: Throwable ->
            val cause = getCause(throwable)
            Observable.concat(
                Observable.just(Report.error(cause, time.now())),
                Observable.error(throwable)
            )
        }
        .map { Loadable.loaded(it) }
        .retryWhen { it.delay(retryDelay) }
        .distinctUntilChanged()
        .doOnNext { logInfo { "Streamed Kp index: $it" } }
        .replayingShare(Loadable.Loading)

    override fun stream() = stream

    private fun getReport(): Single<Report<KpIndex>> =
        api.get()
            .doOnError { logWarn(it) { "Failed to get Kp index from KpIndex API" } }
            .flatMap { response ->
                if (response.isSuccessful) {
                    Single.just(Report.success(KpIndex(response.body()!!.value), time.now()))
                } else {
                    val exception = ServerResponseException(response.code(), response.errorBody()!!.string())
                    logError(exception) { "Failed to get Kp index from KpIndex API" }
                    Single.error(exception)
                }
            }
}

// TODO Fix duplication with RetrofittedOpenWeatherMapWeatherProvider
private fun getCause(throwable: Throwable): Cause =
    when (throwable) {
        is IOException -> Cause.Connectivity
        is ServerResponseException -> Cause.ServerResponse
        else -> Cause.Unknown
    }

// TODO Fix duplication with RetrofittedOpenWeatherMapWeatherProvider
private class ServerResponseException(code: Int, body: String) : Exception("Server error $code. Body: $body")
