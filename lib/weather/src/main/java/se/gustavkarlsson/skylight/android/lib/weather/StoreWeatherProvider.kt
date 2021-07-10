package se.gustavkarlsson.skylight.android.lib.weather

import com.dropbox.android.external.store4.Store
import com.dropbox.android.external.store4.StoreRequest
import com.dropbox.android.external.store4.StoreResponse
import com.dropbox.android.external.store4.fresh
import com.dropbox.android.external.store4.get
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import se.gustavkarlsson.skylight.android.core.entities.Cause
import se.gustavkarlsson.skylight.android.core.entities.Loadable
import se.gustavkarlsson.skylight.android.core.entities.Report
import se.gustavkarlsson.skylight.android.core.logging.logInfo
import se.gustavkarlsson.skylight.android.lib.location.ApproximatedLocation
import se.gustavkarlsson.skylight.android.lib.location.Location
import se.gustavkarlsson.skylight.android.lib.location.LocationResult
import se.gustavkarlsson.skylight.android.lib.location.approximate
import se.gustavkarlsson.skylight.android.lib.location.map
import se.gustavkarlsson.skylight.android.lib.time.Time
import java.io.IOException

internal class StoreWeatherProvider(
    private val store: Store<ApproximatedLocation, Weather>,
    private val time: Time,
    private val approximationMeters: Double,
) : WeatherProvider {

    override suspend fun get(locationResult: LocationResult, fresh: Boolean): Report<Weather> =
        getReport(locationResult, fresh)

    private suspend fun getReport(locationResult: LocationResult, fresh: Boolean): Report<Weather> {
        val report = locationResult.map(
            onSuccess = { location ->
                try {
                    val weather = if (fresh) {
                        store.fresh(location.approximate(approximationMeters))
                    } else {
                        store.get(location.approximate(approximationMeters))
                    }
                    Report.Success(weather, time.now())
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    Report.Error(getCause(e), time.now())
                }
            },
            onMissingPermissionError = { Report.Error(Cause.LocationPermission, time.now()) },
            onUnknownError = { Report.Error(Cause.Location, time.now()) },
        )
        logInfo { "Provided weather: $report" }
        return report
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun stream(
        locations: Flow<Loadable<LocationResult>>
    ): Flow<Loadable<Report<Weather>>> =
        locations
            .flatMapLatest { loadable ->
                when (loadable) {
                    Loadable.Loading -> flowOf(Loadable.Loading)
                    is Loadable.Loaded -> {
                        loadable.value.map(
                            onSuccess = ::streamReports,
                            onMissingPermissionError = {
                                flowOf(Loadable.loaded(Report.Error(Cause.LocationPermission, time.now())))
                            },
                            onUnknownError = {
                                flowOf(Loadable.loaded(Report.Error(Cause.Location, time.now())))
                            }
                        )
                    }
                }
            }
            .distinctUntilChanged()
            .onEach { logInfo { "Streamed weather: $it" } }

    private fun streamReports(location: Location): Flow<Loadable<Report<Weather>>> =
        store.stream(StoreRequest.cached(location.approximate(approximationMeters), refresh = false))
            .map { response ->
                when (response) {
                    is StoreResponse.Loading -> Loadable.loading()
                    is StoreResponse.Data -> Loadable.loaded(Report.success(response.value, time.now()))
                    is StoreResponse.Error.Exception ->
                        Loadable.loaded(Report.error(getCause(response.error), time.now()))
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
