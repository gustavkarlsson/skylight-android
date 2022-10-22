package se.gustavkarlsson.skylight.android.lib.kpindex

import com.dropbox.android.external.store4.Fetcher
import com.dropbox.android.external.store4.FetcherResult
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.datetime.Instant
import se.gustavkarlsson.skylight.android.core.logging.logError
import se.gustavkarlsson.skylight.android.core.logging.logInfo
import se.gustavkarlsson.skylight.android.core.logging.logWarn
import java.io.IOException
import kotlin.time.Duration

internal fun createKpIndexForecastFetcher(
    api: KpIndexApi,
    retryDelay: Duration,
    pollingInterval: Duration,
    dispatcher: CoroutineDispatcher,
): Fetcher<Unit, KpIndexForecast> = Fetcher.ofResultFlow {
    flow {
        while (true) {
            try {
                val apiForecast = api.requestKpIndexForecast()
                val forecast = apiForecast.toKpIndexForecast()
                emit(FetcherResult.Data(forecast))
                delay(pollingInterval)
            } catch (e: IOException) {
                emit(FetcherResult.Error.Exception(e))
                delay(retryDelay)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                emit(FetcherResult.Error.Exception(e))
            }
        }
    }.flowOn(dispatcher)
}

private suspend fun KpIndexApi.requestKpIndexForecast(): KpIndexForecastBody {
    return try {
        val response = getKpIndexForecast()
        if (response.isSuccessful) {
            logInfo { "Got KpIndex forecast from KpIndex API: ${response.body()!!}" }
            response.body()!!
        } else {
            val code = response.code()
            val body = response.errorBody()?.string() ?: "<empty>"
            logError { "Failed to get KpIndex forecast from KpIndex API. HTTP $code: $body" }
            throw ServerResponseException(code, body)
        }
    } catch (e: IOException) {
        logWarn(e) { "Failed to get KpIndex forecast from KpIndex API" }
        throw e
    }
}

private fun KpIndexForecastBody.toKpIndexForecast(): KpIndexForecast {
    val kpIndexes = kpIndexes
        .sortedBy { it.timestamp }
        .map { KpIndex(it.value, Instant.fromEpochMilliseconds(it.timestamp)) }
    return KpIndexForecast(kpIndexes)
}
