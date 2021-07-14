package se.gustavkarlsson.skylight.android.lib.weather

import com.dropbox.android.external.store4.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import org.threeten.bp.Instant
import se.gustavkarlsson.skylight.android.core.entities.*
import se.gustavkarlsson.skylight.android.core.logging.logInfo
import se.gustavkarlsson.skylight.android.lib.location.*
import se.gustavkarlsson.skylight.android.lib.time.Time
import java.io.IOException

internal class StoreWeatherProvider(
    private val store: Store<ApproximatedLocation, Weather>,
    private val time: Time,
    private val approximationMeters: Double,
) : WeatherProvider {

    override suspend fun get(locationResult: LocationResult, fresh: Boolean): Report<Weather> =
        getReport(locationResult, fresh, time.now())

    private suspend fun getReport(locationResult: LocationResult, fresh: Boolean, timestamp: Instant): Report<Weather> {
        val report = locationResult.fold(
            ifLeft = { error ->
                val cause = when (error) {
                    LocationError.NoPermission -> Cause.NoLocationPermission
                    LocationError.Unknown -> Cause.NoLocation
                }
                Report.Error(cause, timestamp)
            },
            ifRight = { location ->
                try {
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
        )
        logInfo { "Provided weather: $report" }
        return report
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun stream(
        locations: Flow<Loadable<LocationResult>>
    ): Flow<Loadable<Report<Weather>>> =
        locations
            .flatMapLatest { loadableLocationResult ->
                loadableLocationResult.fold(
                    ifEmpty = { flowOf(Loading) },
                    ifSome = { locationResult ->
                        locationResult.fold(
                            ifLeft = { error ->
                                val cause = when (error) {
                                    LocationError.NoPermission -> Cause.NoLocationPermission
                                    LocationError.Unknown -> Cause.NoLocation
                                }
                                flowOf(Loaded(Report.Error(cause, time.now())))
                            },
                            ifRight = { location ->
                                streamReports(location)
                            }
                        )
                    }
                )
            }
            .distinctUntilChanged()
            .onEach { logInfo { "Streamed weather: $it" } }

    override fun streamNew(location: Location): Flow<Loadable<Report<Weather>>> =
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
