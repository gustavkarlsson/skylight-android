package se.gustavkarlsson.skylight.android.lib.kpindex

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.FetcherResult
import se.gustavkarlsson.skylight.android.core.logging.logError
import se.gustavkarlsson.skylight.android.core.logging.logInfo
import se.gustavkarlsson.skylight.android.core.logging.logWarn
import se.gustavkarlsson.skylight.android.lib.time.Time
import java.io.IOException
import kotlin.time.Duration

internal fun createKpIndexFetcher(
    api: KpIndexApi,
    retryDelay: Duration,
    pollingInterval: Duration,
    dispatcher: CoroutineDispatcher,
    time: Time,
): Fetcher<Unit, KpIndex> = Fetcher.ofResultFlow {
    flow {
        while (true) {
            try {
                val apiKpIndex = api.requestKpIndex()
                val kpIndex = KpIndex(apiKpIndex.value, time.now()) // Ignore timestamp from API
                emit(FetcherResult.Data(kpIndex))
                delay(pollingInterval)
            } catch (e: IOException) {
                emit(FetcherResult.Error.Exception(e))
                delay(retryDelay)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                emit(FetcherResult.Error.Exception(e))
                return@flow
            }
        }
    }.flowOn(dispatcher)
}

private suspend fun KpIndexApi.requestKpIndex(): KpIndexBody {
    return try {
        val response = getKpIndex()
        if (response.isSuccessful) {
            logInfo { "Got KpIndex from KpIndex API: ${response.body()!!}" }
            response.body()!! // Ignore timestamp
        } else {
            val code = response.code()
            val body = response.errorBody()?.string() ?: "<empty>"
            logError { "Failed to get KpIndex from KpIndex API. HTTP $code: $body" }
            throw ServerResponseException(code, body)
        }
    } catch (e: IOException) {
        logWarn(e) { "Failed to get KpIndex from KpIndex API" }
        throw e
    }
}
