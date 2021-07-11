package se.gustavkarlsson.skylight.android.lib.reversegeocoder

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.rightIfNotNull
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
            onMissingPermissionError = { ReverseGeocodingError.NoLocationPermission.left() },
            onUnknownError = { ReverseGeocodingError.NoLocation.left() },
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
                            val error = ReverseGeocodingError.NoLocationPermission.left()
                            flowOf(Loaded(error))
                        },
                        onUnknownError = {
                            val error = ReverseGeocodingError.NoLocation.left()
                            flowOf(Loaded(error))
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
            val shouldRetry = result.fold(
                ifLeft = { it is ReverseGeocodingError.Io },
                ifRight = { false }
            )
            if (shouldRetry) {
                delay(retryDelay.toMillis())
            }
        } while (shouldRetry)
    }

    private suspend fun getName(location: Location): ReverseGeocodingResult = Either
        .catch {
            store.get(location.approximate(approximationMeters)).value
        }
        .mapLeft { throwable ->
            when (throwable) {
                is IOException -> ReverseGeocodingError.Io(throwable)
                else -> ReverseGeocodingError.Unknown(throwable)
            }
        }
        .flatMap { name ->
            name.rightIfNotNull { ReverseGeocodingError.NotFound }
        }
}
