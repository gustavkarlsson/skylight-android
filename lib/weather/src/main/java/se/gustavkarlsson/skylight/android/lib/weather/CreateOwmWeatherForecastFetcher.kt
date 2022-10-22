package se.gustavkarlsson.skylight.android.lib.weather

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
import se.gustavkarlsson.skylight.android.lib.location.ApproximatedLocation
import java.io.IOException
import kotlin.time.Duration

internal fun createOwmWeatherForecastFetcher(
    api: OpenWeatherMapApi,
    appId: String,
    retryDelay: Duration,
    pollingInterval: Duration,
    dispatcher: CoroutineDispatcher,
): Fetcher<ApproximatedLocation, WeatherForecast> = Fetcher.ofResultFlow { location ->
    flow {
        while (true) {
            try {
                val apiForecast = api.requestWeatherForecast(location, appId)
                val forecast = apiForecast.toWeatherForecast()
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

private fun OpenWeatherMapWeatherForecast.toWeatherForecast(): WeatherForecast {
    val weathers = list
        .sortedBy { it.dt }
        .map { Weather(it.clouds.all, Instant.fromEpochSeconds(it.dt)) }
    return WeatherForecast(weathers)
}

private suspend fun OpenWeatherMapApi.requestWeatherForecast(
    location: ApproximatedLocation,
    appId: String,
): OpenWeatherMapWeatherForecast {
    return try {
        val response = getWeatherForecast(location.latitude, location.longitude, appId)
        if (response.isSuccessful) {
            logInfo { "Got weather forecast from OpenWeatherMap API: ${response.body()!!}" }
            response.body()!!
        } else {
            val code = response.code()

            val body = response.errorBody()?.string() ?: "<empty>"
            logError { "Failed to get Weather forecast from OpenWeatherMap API. HTTP $code: $body" }
            throw ServerResponseException(code, body)
        }
    } catch (e: IOException) {
        logWarn(e) { "Failed to get Weather forecast from OpenWeatherMap API" }
        throw e
    }
}
