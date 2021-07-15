package se.gustavkarlsson.skylight.android.lib.weather

import arrow.core.left
import arrow.core.right
import com.dropbox.android.external.store4.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import se.gustavkarlsson.skylight.android.core.entities.Loadable
import se.gustavkarlsson.skylight.android.core.entities.Loaded
import se.gustavkarlsson.skylight.android.core.entities.Loading
import se.gustavkarlsson.skylight.android.core.logging.logInfo
import se.gustavkarlsson.skylight.android.lib.location.ApproximatedLocation
import se.gustavkarlsson.skylight.android.lib.location.Location
import se.gustavkarlsson.skylight.android.lib.location.approximate
import java.io.IOException

internal class StoreWeatherProvider(
    private val store: Store<ApproximatedLocation, Weather>,
    private val approximationMeters: Double,
) : WeatherProvider {

    override suspend fun get(location: Location, fresh: Boolean): WeatherResult {
        val result = getResult(location, fresh)
        logInfo { "Provided weather: $result" }
        return result
    }

    private suspend fun getResult(location: Location, fresh: Boolean): WeatherResult {
        return try {
            val weather = if (fresh) {
                store.fresh(location.approximate(approximationMeters))
            } else {
                store.get(location.approximate(approximationMeters))
            }
            weather.right()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            e.toWeatherError().left()
        }
    }

    override fun stream(location: Location): Flow<Loadable<WeatherResult>> =
        streamResults(location)
            .distinctUntilChanged()
            .onEach { logInfo { "Streamed weather: $it" } }

    private fun streamResults(location: Location): Flow<Loadable<WeatherResult>> =
        store.stream(StoreRequest.cached(location.approximate(approximationMeters), refresh = false))
            .map { response ->
                when (response) {
                    is StoreResponse.Loading -> Loading
                    is StoreResponse.Data -> Loaded(response.value.right())
                    is StoreResponse.Error.Exception ->
                        Loaded(response.error.toWeatherError().left())
                    is StoreResponse.Error.Message, is StoreResponse.NoNewData ->
                        error("Unsupported response type: $response")
                }
            }
}

// TODO Fix duplication with RetrofittedKpIndexProvider
private fun Throwable.toWeatherError(): WeatherError =
    when (this) {
        is IOException -> WeatherError.Connectivity
        is ServerResponseException -> WeatherError.ServerResponse
        else -> WeatherError.Unknown
    }
