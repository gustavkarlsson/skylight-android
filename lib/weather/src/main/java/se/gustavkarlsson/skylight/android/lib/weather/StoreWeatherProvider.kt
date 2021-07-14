package se.gustavkarlsson.skylight.android.lib.weather

import com.dropbox.android.external.store4.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import org.threeten.bp.Instant
import se.gustavkarlsson.skylight.android.core.entities.*
import se.gustavkarlsson.skylight.android.core.logging.logInfo
import se.gustavkarlsson.skylight.android.lib.location.ApproximatedLocation
import se.gustavkarlsson.skylight.android.lib.location.Location
import se.gustavkarlsson.skylight.android.lib.location.approximate
import se.gustavkarlsson.skylight.android.lib.time.Time
import java.io.IOException

internal class StoreWeatherProvider(
    private val store: Store<ApproximatedLocation, Weather>,
    private val time: Time,
    private val approximationMeters: Double,
) : WeatherProvider {

    override suspend fun get(location: Location, fresh: Boolean): Report<Weather> {
        val report = getReport(location, fresh, time.now())
        logInfo { "Provided weather: $report" }
        return report
    }

    private suspend fun getReport(location: Location, fresh: Boolean, timestamp: Instant): Report<Weather> {
        return try {
            val weather = if (fresh) {
                store.fresh(location.approximate(approximationMeters))
            } else {
                store.get(location.approximate(approximationMeters))
            }
            Report.Success(weather, timestamp)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Report.Error(getCause(e), timestamp)
        }
    }

    override fun stream(location: Location): Flow<Loadable<Report<Weather>>> =
        streamReports(location)
            .distinctUntilChanged()
            .onEach { logInfo { "Streamed weather: $it" } }

    private fun streamReports(location: Location): Flow<Loadable<Report<Weather>>> =
        store.stream(StoreRequest.cached(location.approximate(approximationMeters), refresh = false))
            .map { response ->
                when (response) {
                    is StoreResponse.Loading -> Loading
                    is StoreResponse.Data -> Loaded(Report.success(response.value, time.now()))
                    is StoreResponse.Error.Exception ->
                        Loaded(Report.error(getCause(response.error), time.now()))
                    is StoreResponse.Error.Message, is StoreResponse.NoNewData ->
                        error("Unsupported response type: $response")
                }
            }
}

// TODO Fix duplication with RetrofittedKpIndexProvider
private fun getCause(throwable: Throwable): Cause =
    when (throwable) {
        is IOException -> Cause.Connectivity
        is ServerResponseException -> Cause.ServerResponse
        else -> Cause.Unknown
    }
