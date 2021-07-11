package se.gustavkarlsson.skylight.android.lib.reversegeocoder

import com.dropbox.android.external.store4.Store
import com.dropbox.android.external.store4.get
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import org.threeten.bp.Duration
import se.gustavkarlsson.koptional.Optional
import se.gustavkarlsson.koptional.valueOr
import se.gustavkarlsson.skylight.android.core.entities.Loadable
import se.gustavkarlsson.skylight.android.core.entities.Loaded
import se.gustavkarlsson.skylight.android.core.entities.Loading
import se.gustavkarlsson.skylight.android.core.logging.logInfo
import se.gustavkarlsson.skylight.android.lib.location.ApproximatedLocation
import se.gustavkarlsson.skylight.android.lib.location.Location
import se.gustavkarlsson.skylight.android.lib.location.LocationResult
import se.gustavkarlsson.skylight.android.lib.location.approximate
import se.gustavkarlsson.skylight.android.lib.location.map
import java.io.IOException

internal class StoreReverseGeocoder(
    private val store: Store<ApproximatedLocation, Optional<String>>,
    private val retryDelay: Duration,
    private val approximationMeters: Double,
) : ReverseGeocoder {

    override suspend fun get(locationResult: LocationResult): ReverseGeocodingResult {
        val result = locationResult.map(
            onSuccess = { location -> getName(location) },
            onMissingPermissionError = { ReverseGeocodingResult.Failure.LocationPermission },
            onUnknownError = { ReverseGeocodingResult.Failure.Location },
        )
        logInfo { "Provided location name: $result" }
        return result
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun stream(locations: Flow<Loadable<LocationResult>>): Flow<Loadable<ReverseGeocodingResult>> {
        return locations
            .flatMapLatest { loadableLocationResult ->
                when (loadableLocationResult) {
                    is Loading -> flowOf(Loading)
                    is Loaded -> loadableLocationResult.value.map(
                        onSuccess = { location ->
                            getNameWithRetry(location)
                        },
                        onMissingPermissionError = {
                            flowOf(Loaded(ReverseGeocodingResult.Failure.LocationPermission))
                        },
                        onUnknownError = {
                            flowOf(Loaded(ReverseGeocodingResult.Failure.Location))
                        },
                    )
                }
            }
            .distinctUntilChanged()
            .onEach { logInfo { "Streamed location name: $it" } }
    }

    private suspend fun getNameWithRetry(location: Location): Flow<Loadable<ReverseGeocodingResult>> = flow {
        emit(Loading)
        do {
            val result = getName(location)
            emit(Loaded(result))
            val shouldRetry = result is ReverseGeocodingResult.Failure.Io
            if (shouldRetry) {
                delay(retryDelay.toMillis())
            }
        } while (shouldRetry)
    }

    private suspend fun getName(location: Location): ReverseGeocodingResult = try {
        store.get(location.approximate(approximationMeters))
            .map { ReverseGeocodingResult.Success(it) }
            .valueOr { ReverseGeocodingResult.Failure.NotFound }
    } catch (e: IOException) {
        ReverseGeocodingResult.Failure.Io(e)
    }
}
