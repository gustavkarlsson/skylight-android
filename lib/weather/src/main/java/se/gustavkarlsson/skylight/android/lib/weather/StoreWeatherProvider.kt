package se.gustavkarlsson.skylight.android.lib.weather

import com.dropbox.android.external.store4.Store
import com.dropbox.android.external.store4.StoreRequest
import com.dropbox.android.external.store4.StoreResponse
import com.dropbox.android.external.store4.fresh
import com.dropbox.android.external.store4.get
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
import se.gustavkarlsson.skylight.android.lib.location.Location
import se.gustavkarlsson.skylight.android.lib.location.LocationResult
import se.gustavkarlsson.skylight.android.lib.time.Time
import java.io.IOException

internal class StoreWeatherProvider(
    private val store: Store<Location, Weather>,
    private val time: Time
) : WeatherProvider {

    override suspend fun get(location: LocationResult, fresh: Boolean): Report<Weather> =
        getSingleReport(location) {
            if (fresh) {
                fresh(it)
            } else {
                get(it)
            }
        }

    private suspend fun getSingleReport(
        location: LocationResult,
        getWeather: suspend Store<Location, Weather>.(Location) -> Weather
    ): Report<Weather> {
        val report = when (location) {
            is LocationResult.Success ->
                try {
                    val weather = store.getWeather(location.location)
                    Report.Success(weather, time.now())
                } catch (e: Exception) {
                    Report.Error(getCause(e), time.now())
                }
            LocationResult.Failure.MissingPermission -> Report.Error(Cause.LocationPermission, time.now())
            LocationResult.Failure.Unknown -> Report.Error(Cause.Location, time.now())
        }
        logInfo { "Provided weather: $report" }
        return report
    }

    @ExperimentalCoroutinesApi
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
        store.stream(StoreRequest.cached(location, refresh = false))
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
