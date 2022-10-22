package se.gustavkarlsson.skylight.android.lib.weather

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
import se.gustavkarlsson.skylight.android.lib.location.ApproximatedLocation
import se.gustavkarlsson.skylight.android.lib.time.Time
import java.io.IOException
import kotlin.time.Duration

internal fun createOwmWeatherFetcher(
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
                val apiWeather = api.requestWeather(location, appId)
                val weather = Weather(apiWeather.clouds.all, time.now())
                emit(FetcherResult.Data(weather))
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

private suspend fun OpenWeatherMapApi.requestWeather(
    location: ApproximatedLocation,
    appId: String,
): OpenWeatherMapWeather {
    return try {
        val response = getWeather(location.latitude, location.longitude, appId)
        if (response.isSuccessful) {
            logInfo { "Got weather from OpenWeatherMap API: ${response.body()!!}" }
            response.body()!!
        } else {
            val code = response.code()

            val body = response.errorBody()?.string() ?: "<empty>"
            logError { "Failed to get Weather from OpenWeatherMap API. HTTP $code: $body" }
            throw ServerResponseException(code, body)
        }
    } catch (e: IOException) {
        logWarn(e) { "Failed to get Weather from OpenWeatherMap API" }
        throw e
    }
}
