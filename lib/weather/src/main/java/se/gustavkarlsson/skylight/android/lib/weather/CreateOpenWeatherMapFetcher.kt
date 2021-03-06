package se.gustavkarlsson.skylight.android.lib.weather

import com.dropbox.android.external.store4.Fetcher
import com.dropbox.android.external.store4.FetcherResult
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.threeten.bp.Duration
import se.gustavkarlsson.skylight.android.core.logging.logError
import se.gustavkarlsson.skylight.android.core.logging.logInfo
import se.gustavkarlsson.skylight.android.core.logging.logWarn
import se.gustavkarlsson.skylight.android.lib.location.ApproximatedLocation
import se.gustavkarlsson.skylight.android.lib.time.Time
import java.io.IOException

internal fun createOpenWeatherMapFetcher(
    api: OpenWeatherMapApi,
    appId: String,
    retryDelay: Duration,
    pollingInterval: Duration,
    dispatcher: CoroutineDispatcher,
    time: Time,
): Fetcher<ApproximatedLocation, Weather> = Fetcher.ofResultFlow { location ->
    flow {
        while (true) {
            try {
                val weather = Weather(api.requestClouds(location, appId), time.now())
                emit(FetcherResult.Data(weather))
                delay(pollingInterval.toMillis())
            } catch (e: IOException) {
                emit(FetcherResult.Error.Exception(e))
                delay(retryDelay.toMillis())
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                emit(FetcherResult.Error.Exception(e))
            }
        }
    }.flowOn(dispatcher)
}

private suspend fun OpenWeatherMapApi.requestClouds(location: ApproximatedLocation, appId: String): Int =
    try {
        val response = get(location.latitude, location.longitude, "json", appId)
        if (response.isSuccessful) {
            logInfo { "Got weather from OpenWeatherMap API: ${response.body()!!}" }
            response.body()!!.clouds.all
        } else {
            val code = response.code()

            @Suppress("BlockingMethodInNonBlockingContext")
            val body = response.errorBody()?.string() ?: "<empty>"
            logError { "Failed to get Weather from OpenWeatherMap API. HTTP $code: $body" }
            throw ServerResponseException(code, body)
        }
    } catch (e: IOException) {
        logWarn(e) { "Failed to get Weather from OpenWeatherMap API" }
        throw e
    }
