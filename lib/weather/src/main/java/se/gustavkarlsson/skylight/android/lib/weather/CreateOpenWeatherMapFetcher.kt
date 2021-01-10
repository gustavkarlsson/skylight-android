package se.gustavkarlsson.skylight.android.lib.weather

import com.dropbox.android.external.store4.Fetcher
import com.dropbox.android.external.store4.FetcherResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.threeten.bp.Duration
import se.gustavkarlsson.skylight.android.core.logging.logError
import se.gustavkarlsson.skylight.android.core.logging.logInfo
import se.gustavkarlsson.skylight.android.core.logging.logWarn
import se.gustavkarlsson.skylight.android.lib.location.Location
import java.io.IOException

@ExperimentalCoroutinesApi
internal fun createOpenWeatherMapFetcher(
    api: OpenWeatherMapApi,
    appId: String,
    retryDelay: Duration,
    pollingInterval: Duration,
    dispatcher: CoroutineDispatcher
): Fetcher<Location, Weather> = Fetcher.ofResultFlow { location ->
    flow {
        while (true) {
            try {
                val weather = api.requestWeather(location, appId)
                emit(FetcherResult.Data(weather))
                delay(pollingInterval.toMillis())
            } catch (e: IOException) {
                emit(FetcherResult.Error.Exception(e))
                delay(retryDelay.toMillis())
            }
        }
    }.flowOn(dispatcher)
}

private suspend fun OpenWeatherMapApi.requestWeather(location: Location, appId: String): Weather =
    try {
        val response = get(location.latitude, location.longitude, "json", appId)
        if (response.isSuccessful) {
            logInfo { "Got weather from OpenWeatherMap API: ${response.body()!!}" }
            Weather(response.body()!!.clouds.all)
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
