package se.gustavkarlsson.skylight.android.lib.kpindex

import com.dropbox.android.external.store4.Fetcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flowOn
import org.threeten.bp.Duration
import se.gustavkarlsson.skylight.android.core.entities.Cause
import se.gustavkarlsson.skylight.android.core.entities.Report
import se.gustavkarlsson.skylight.android.core.logging.logError
import se.gustavkarlsson.skylight.android.core.logging.logInfo
import se.gustavkarlsson.skylight.android.core.logging.logWarn
import se.gustavkarlsson.skylight.android.lib.time.Time
import java.io.IOException

@ExperimentalCoroutinesApi
internal fun createKpIndexFetcher(
    api: KpIndexApi,
    retryDelay: Duration,
    pollingInterval: Duration,
    dispatcher: CoroutineDispatcher,
    time: Time,
): Fetcher<Unit, Report<KpIndex>> = Fetcher.ofFlow {
    channelFlow {
        while (!isClosedForSend) {
            try {
                val kpIndex = api.requestKpIndex()
                send(Report.success(kpIndex, time.now()))
                delay(pollingInterval.toMillis())
            } catch (e: Exception) {
                send(Report.error(getCause(e), time.now()))
                delay(retryDelay.toMillis())
            }
        }
    }.flowOn(dispatcher)
}

private suspend fun KpIndexApi.requestKpIndex(): KpIndex =
    try {
        val response = get()
        if (response.isSuccessful) {
            logInfo { "Got KpIndex from KpIndex API: ${response.body()!!}" }
            KpIndex(response.body()!!.value)
        } else {
            val code = response.code()
            @Suppress("BlockingMethodInNonBlockingContext")
            val body = response.errorBody()?.string() ?: "<empty>"
            logError { "Failed to get KpIndex from KpIndex API. HTTP $code: $body" }
            throw ServerResponseException(code, body)
        }
    } catch (e: Exception) {
        logWarn(e) { "Failed to get KpIndex from KpIndex API" }
        throw e
    }

// TODO Fix duplication with RetrofittedOpenWeatherMapWeatherProvider
private fun getCause(throwable: Throwable): Cause =
    when (throwable) {
        is IOException -> Cause.Connectivity
        is ServerResponseException -> Cause.ServerResponse
        else -> Cause.Unknown
    }
